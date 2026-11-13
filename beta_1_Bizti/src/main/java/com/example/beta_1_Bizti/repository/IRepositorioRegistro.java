package com.example.beta_1_Bizti.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.beta_1_Bizti.models.Registro;

@Repository
public interface IRepositorioRegistro extends JpaRepository<Registro, UUID> {

    // Trae reto y usuario en la misma consulta (evita N+1)
    @Override
    @EntityGraph(attributePaths = { "reto", "usuario" })
    Page<Registro> findAll(Pageable pageable);

    boolean existsByReto_Id(UUID idReto);

    boolean existsByUsuario_Id(UUID idUsuario);
}
