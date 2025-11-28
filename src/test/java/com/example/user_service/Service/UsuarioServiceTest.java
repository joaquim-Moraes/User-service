package com.example.user_service.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.user_service.Entity.Usuario;
import com.example.user_service.Repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    UsuarioService usuarioService;

    @Test
    void cadastrarUsuario_setsEncodedPasswordAndSendsEmail() {
        Usuario u = new Usuario();
        u.setEmailUsuario("test@example.com");
        u.setSenhaUsuario("plain123");
        u.setNomeUsuario("Nome");
        u.setTelefoneUsuario("11999999999");
        u.setCpfUsuario("12345678901");

        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario saved = usuarioService.cadastrarUsuario(u);

        assertNotNull(saved.getVerificationCode());
        assertFalse(saved.isAtivo());
        assertNotEquals("plain123", saved.getSenhaUsuario());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("plain123", saved.getSenhaUsuario()));

        verify(emailService, times(1)).sendVerificationEmail(eq("test@example.com"), eq(saved.getVerificationCode()));
    }

    @Test
    void iniciarResetSenha_generatesTokenAndSendsEmail() {
        Usuario u = new Usuario();
        u.setEmailUsuario("a@b.com");

        when(usuarioRepository.findByEmailUsuario("a@b.com")).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.iniciarResetSenha("a@b.com");

        assertNotNull(u.getPasswordResetToken());
        assertNotNull(u.getPasswordResetExpiry());
        verify(emailService, times(1)).sendPasswordResetEmail(eq("a@b.com"), eq(u.getPasswordResetToken()));
    }

    @Test
    void resetarSenha_withValidToken_changesPasswordAndClearsToken() {
        Usuario u = new Usuario();
        u.setPasswordResetToken("token123");
        u.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(10));
        u.setSenhaUsuario(new BCryptPasswordEncoder().encode("old"));

        when(usuarioRepository.findByPasswordResetToken("token123")).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.resetarSenha("token123", "newPass123");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("newPass123", u.getSenhaUsuario()));
        assertNull(u.getPasswordResetToken());
        assertNull(u.getPasswordResetExpiry());
    }

    @Test
    void verificarUsuarioPeloCodigo_withValidCode_activatesUser() {
        Usuario u = new Usuario();
        u.setVerificationCode("code123");
        u.setCodeExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(usuarioRepository.findByVerificationCode("code123")).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.verificarUsuarioPeloCodigo("code123");

        assertTrue(u.isAtivo());
        assertNull(u.getVerificationCode());
        assertNull(u.getCodeExpiryDate());
    }

    @Test
    void autenticarUsuario_inactive_throws() {
        Usuario u = new Usuario();
        u.setCpfUsuario("cpf");
        u.setAtivo(false);
        u.setSenhaUsuario(new BCryptPasswordEncoder().encode("pass"));

        when(usuarioRepository.findByCpfUsuario("cpf")).thenReturn(Optional.of(u));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticarUsuario("cpf", "pass"));
        assertEquals("Conta não verificada. Verifique seu e-mail.", ex.getMessage());
    }

    @Test
    void autenticarUsuario_wrongPassword_throws() {
        Usuario u = new Usuario();
        u.setCpfUsuario("cpf2");
        u.setAtivo(true);
        u.setSenhaUsuario(new BCryptPasswordEncoder().encode("pass"));

        when(usuarioRepository.findByCpfUsuario("cpf2")).thenReturn(Optional.of(u));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticarUsuario("cpf2", "wrong"));
        assertEquals("Senha incorreta.", ex.getMessage());
    }

    @Test
    void autenticarUsuario_success_returnsUser() {
        Usuario u = new Usuario();
        u.setCpfUsuario("cpf3");
        u.setAtivo(true);
        u.setSenhaUsuario(new BCryptPasswordEncoder().encode("pass"));

        when(usuarioRepository.findByCpfUsuario("cpf3")).thenReturn(Optional.of(u));

        Usuario result = usuarioService.autenticarUsuario("cpf3", "pass");
        assertSame(u, result);
    }
}
