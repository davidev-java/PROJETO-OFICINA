package com.novanest.projetooficina.entity;

import com.novanest.projetooficina.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String nome;

    // URL da foto de perfil vinda do Google
    private String avatarUrl;

    // Hash da senha (BCrypt) - so preenchido em quem se cadastrou com email/senha.
    // Quem entra so pelo Google fica com isso null (nao tem senha local).
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Marca usuarios/dados do modo demo publico, resetados periodicamente
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean demo = false;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // só preenchido quando role == CLIENTE

    private LocalDateTime criadoEm;

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
    }

}