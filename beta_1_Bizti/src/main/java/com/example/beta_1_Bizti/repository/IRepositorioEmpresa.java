package com.example.beta_1_Bizti.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.beta_1_Bizti.models.Empresa;

@Repository
public interface IRepositorioEmpresa extends JpaRepository<Empresa, UUID> {

    boolean existsByNit(String nit);

    boolean existsByNitAndIdNot(String nit, UUID id);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, UUID id);

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndIdNot(String telefono, UUID id);
}
