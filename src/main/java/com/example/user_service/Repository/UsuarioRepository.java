package com.example.user_service.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.Entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    List<Usuario> findByNomeUsuario(String nome);
    Optional<Usuario> findByEmailUsuario(String email);
    Optional<Usuario> findByIdUsuario(Long id);
    Optional<Usuario> findByCpfUsuario(String cpf);
    Optional<Usuario> findByVerificationCode(String token);

}
