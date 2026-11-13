package com.example.beta_1_Bizti.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.beta_1_Bizti.models.Usuario;

@Repository
public interface IRepositorioUsuario extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByCorreo(String correo);
}
