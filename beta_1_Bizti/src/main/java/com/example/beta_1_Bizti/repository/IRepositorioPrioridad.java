package com.example.beta_1_Bizti.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.beta_1_Bizti.models.Prioridad;

@Repository
public interface IRepositorioPrioridad extends JpaRepository<Prioridad, UUID> {
}
