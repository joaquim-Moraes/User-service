package com.example.user_service.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.user_service.Service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public Usuario cadastrarUsuario(@RequestBody Usuario usuario){
        return usuarioService.cadastrarUsuario(usuario);
    }

    @GetMapping("/listagem")
    public List<Usuario> listarTodosUsuarios(){
        return usuarioService.listarUsuarios();
    }


    @GetMapping("/buscar")
    public Usuario filtrarPorId(@RequestParam int id) {
        return usuarioService.buscarPorId(id);
    }


    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable int id) {
        usuarioService.deletarUsuario(id);
    }

    @PutMapping("/{id}")
    public Usuario atualizarUsuario(@PathVariable int id, @RequestBody Usuario dadosAtualizados) {
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
    public Usuario login(@RequestBody Usuario loginData) {
        return usuarioService.autenticarUsuario(loginData.getCpfUsuario(), loginData.getSenhaUsuario());
    }


}
