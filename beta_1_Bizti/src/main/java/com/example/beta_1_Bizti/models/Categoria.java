package com.example.beta_1_Bizti.models;


import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table (name = "categorias")
public class Categoria {

@Id
@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

@Column (nullable = false)
private String nombre;

@Column (nullable = false)
private String descripcion;

@OneToMany(mappedBy = "categoria")
@JsonIgnore
private List<Reto> retos;

//Constructor
public Categoria() {
}

//Get y set
public UUID getId() {
    return id;
}


public void setId(UUID id) {
    this.id = id;
}


public String getNombre() {
    return nombre;
}


public void setNombre(String nombre) {
    this.nombre = nombre;
}


public String getDescripcion() {
    return descripcion;
}


public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
}


public List<Reto> getRetos() {
    return retos;
}


public void setRetos(List<Reto> retos) {
    this.retos = retos;
}


}

