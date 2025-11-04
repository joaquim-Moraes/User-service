package com.example.user_service.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_service.Entity.Usuario;
import com.example.user_service.Repository.UsuarioRepository;

@Service
public class UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Cadastro do cliente
    public Usuario cadastrarUsuario(Usuario usuario){
        validarEmail(usuario.getEmailUsuario());
        validarSenha(usuario.getSenhaUsuario());
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setSenhaUsuario(encoder.encode(usuario.getSenhaUsuario()));
        
        return usuarioRepository.save(usuario);
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

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(senha, usuario.getSenhaUsuario())) {
            throw new IllegalArgumentException("Senha incorreta.");
            
        }

        return usuario; // Login bem-sucedido
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




