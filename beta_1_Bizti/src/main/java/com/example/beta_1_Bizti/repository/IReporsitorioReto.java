package com.example.beta_1_Bizti.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.beta_1_Bizti.models.Reto;

@Repository 
public interface IReporsitorioReto extends JpaRepository<Reto,UUID>{

}
