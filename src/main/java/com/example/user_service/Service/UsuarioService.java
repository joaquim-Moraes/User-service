package com.example.user_service.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_service.Entity.Usuario;
import com.example.user_service.Repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    // Cadastro do cliente
    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario){
        validarEmail(usuario.getEmailUsuario());
        validarSenha(usuario.getSenhaUsuario());
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setSenhaUsuario(encoder.encode(usuario.getSenhaUsuario()));

        //Verificação do Email
        String token = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);

        usuario.setAtivo(false);// Ele fica como inativo, até o usuario tentar fazer o login com o codigo
        usuario.setVerificationCode(token);
        usuario.setCodeExpiryDate(expiryDate);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        emailService.sendVerificationEmail(usuarioSalvo.getEmailUsuario(), token);

        return usuarioSalvo;

    }

    public List<Usuario> listarUsuarios(){
        return usuarioRepository.findAll();
    }

    public void atualizarUsuario(Usuario usuario,String nome,String email,String senha){

    if (nome != null && !nome.isBlank()) usuario.setNomeUsuario(nome);
    if (email != null && !email.isBlank()) usuario.setEmailUsuario(email);
    if (senha != null && !senha.isBlank()) usuario.setSenhaUsuario(senha);
       usuarioRepository.save(usuario);
    }

    public void deletarUsuario(int id){
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado."));
    }

    public Usuario autenticarUsuario(String cpf, String senha){
        Usuario usuario = usuarioRepository.findByCpfUsuario(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Checar se o usuario fez a verificação do email
        if (!usuario.isAtivo()) {
            throw new IllegalArgumentException("Conta não verificada. Verifique seu e-mail.");
        }
        
        // checar se a senha bate com a que esta criptografada
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(senha, usuario.getSenhaUsuario())) {
            throw new IllegalArgumentException("Senha incorreta.");
            
        }

        return usuario; // Login bem-sucedido
    }
public void verificarUsuarioPeloCodigo(String codigo) {
    
        
        Usuario usuario = usuarioRepository.findByVerificationCode(codigo)
                .orElseThrow(() -> new IllegalArgumentException("O codigo esta incorreto"));

        //Checa se o código expirou
        if (LocalDateTime.now().isAfter(usuario.getCodeExpiryDate())) {
            // Limpa o código expirado
            usuario.setVerificationCode(null);
            usuario.setCodeExpiryDate(null);
            usuarioRepository.save(usuario);
            
            throw new IllegalArgumentException("Código expirado. Solicite um novo.");
        }


        usuario.setAtivo(true);
        usuario.setVerificationCode(null); 
        usuario.setCodeExpiryDate(null);  
        usuarioRepository.save(usuario);
}

    private void validarEmail(String email) {
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Email inválido.");
        }
    }
    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Senha Invalida.");
        }
    }

}




