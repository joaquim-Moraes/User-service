package com.example.user_service.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.Entity.Usuario;
import com.example.user_service.Security.JwtUtil; // Importado
import com.example.user_service.Service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. INJETA o JwtUtil (bean)
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/cadastro")
    public Usuario cadastrarUsuario(@RequestBody Usuario usuario){
        return usuarioService.cadastrarUsuario(usuario);
    }
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarConta(@RequestParam String codigo) {
        try {
            usuarioService.verificarUsuarioPeloCodigo(codigo);
            return ResponseEntity.ok().body(Map.of("message", "Conta verificada!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/listagem")
    public List<Usuario> listarTodosUsuarios(){
        return usuarioService.listarUsuarios();
    }


    @GetMapping("/buscar")
    public Usuario filtrarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }


    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
    }

    @PutMapping("/{id}")
    public Usuario atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosAtualizados) {
        Usuario usuarioExistente = usuarioService.buscarPorId(id);
        usuarioService.atualizarUsuario(
                usuarioExistente,
                dadosAtualizados.getNomeUsuario(),
                dadosAtualizados.getEmailUsuario(),
                dadosAtualizados.getSenhaUsuario()
        );
        return usuarioExistente;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginData) {
        Usuario usuario = usuarioService.autenticarUsuario(loginData.getCpfUsuario(), loginData.getSenhaUsuario());
        
        String token = jwtUtil.generateToken(usuario.getIdUsuario());

        return ResponseEntity.ok().body(Map.of(
                "token", token,
                "usuario", usuario
        ));
    }
}