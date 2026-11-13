package com.example.beta_1_Bizti.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.beta_1_Bizti.models.Reto;

@Repository
public interface IRepositorioReto extends JpaRepository<Reto, UUID> {

    // Trae empresa, categoria y prioridad en la misma consulta (evita N+1)
    @Override
    @EntityGraph(attributePaths = { "empresa", "categoria", "prioridad" })
    Page<Reto> findAll(Pageable pageable);

    boolean existsByEmpresa_Id(UUID idEmpresa);

    boolean existsByCategoria_Id(UUID idCategoria);

    boolean existsByPrioridad_Id(UUID idPrioridad);
}
