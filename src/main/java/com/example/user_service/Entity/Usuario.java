package com.example.user_service.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;
    @Column(name = "nome_usuario",nullable = false,unique = false)
    private String nomeUsuario;
    @Column(name = "email_usuario",nullable = false,unique = true)
    private String emailUsuario;
    @Column(name = "telefone_usuario",nullable = false,unique = true)
    private String telefoneUsuario;
    @Setter(AccessLevel.NONE)
    @Column(name = "senha_usuario",nullable = false,unique = false)
    private String senhaUsuario;
    @Column(name = "cpf_usuario",nullable = false,unique = true)
    private String cpfUsuario;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = false;

    @Column(name = "verification_code", nullable = true)
    private String verificationCode;

    @Column(name = "code_expiry_date", nullable = true)
    private LocalDateTime codeExpiryDate;

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public Usuario(){

    }

    public Usuario(String emailUsuario, String nomeUsuario, String telefoneUsuario, String senhaUsuario, String cpfUsuario) {
        this.emailUsuario = emailUsuario;
        this.nomeUsuario = nomeUsuario;
        this.telefoneUsuario = telefoneUsuario;
        setSenhaUsuario(senhaUsuario);
        this.idUsuario = idUsuario;
        this.cpfUsuario = cpfUsuario;
    }
}
