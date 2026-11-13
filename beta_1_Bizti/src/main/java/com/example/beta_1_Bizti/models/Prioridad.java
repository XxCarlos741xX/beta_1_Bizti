package com.example.beta_1_Bizti.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "prioridades")
public class Prioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer nivel;

    @OneToMany(mappedBy = "prioridad")
    @JsonIgnore
    private List<Reto> retos;

// Constructor vacío
    public Prioridad() {
    }

// Getters
    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getNivel() {
        return nivel;
    }

    public List<Reto> getRetos() {
        return retos;
    }

// Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public void setRetos(List<Reto> retos) {
        this.retos = retos;
    }
}
