package com.example.user_service.Repository;

import com.example.user_service.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    List<Usuario> findByNomeUsuario(String nome);
    Optional<Usuario> findByEmailUsuario(String email);
    Optional<Usuario> findByIdUsuario(int id);
    Optional<Usuario> findByCpfUsuario(String cpf);
    Optional<Usuario> findByVerificationCode(String token);

}
