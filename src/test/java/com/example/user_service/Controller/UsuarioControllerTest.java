package com.example.user_service.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.user_service.Entity.Usuario;
import com.example.user_service.Security.JwtUtil;
import com.example.user_service.Service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtUtil jwtUtil;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void solicitarResetSenha_returnsOk_whenServiceSucceeds() throws Exception {
        doNothing().when(usuarioService).iniciarResetSenha("a@b.com");

        mockMvc.perform(post("/usuario/solicitar-reset")
                .param("email", "a@b.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void solicitarResetSenha_returnsBadRequest_whenServiceThrows() throws Exception {
        doThrow(new IllegalArgumentException("Email não cadastrado.")).when(usuarioService).iniciarResetSenha("x@y.com");

        mockMvc.perform(post("/usuario/solicitar-reset")
                .param("email", "x@y.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email não cadastrado."));
    }

    @Test
    void resetarSenha_returnsOk_whenServiceSucceeds() throws Exception {
        doNothing().when(usuarioService).resetarSenha("token-123", "novaSenha123");

        String body = mapper.writeValueAsString(java.util.Map.of("novaSenha", "novaSenha123"));

        mockMvc.perform(post("/usuario/reset")
                .param("token", "token-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Senha alterada com sucesso."));
    }

    @Test
    void resetarSenha_returnsBadRequest_whenServiceThrows() throws Exception {
        doThrow(new IllegalArgumentException("Token inválido.")).when(usuarioService).resetarSenha("bad-token", "x");

        String body = mapper.writeValueAsString(java.util.Map.of("novaSenha", "x"));

        mockMvc.perform(post("/usuario/reset")
                .param("token", "bad-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Token inválido."));
    }

    @Test
    void login_returnsTokenAndUsuario_whenCredentialsValid() throws Exception {
        Usuario u = new Usuario();
        u.setCpfUsuario("cpf");
        u.setSenhaUsuario("encoded");
        u.setEmailUsuario("e@e.com");

        when(usuarioService.autenticarUsuario("cpf", "senha")).thenReturn(u);
        when(jwtUtil.generateToken(u.getIdUsuario())).thenReturn("jwt-token-123");

        String body = mapper.writeValueAsString(java.util.Map.of("cpfUsuario", "cpf", "senhaUsuario", "senha"));

        mockMvc.perform(post("/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.usuario").exists());
    }
}
