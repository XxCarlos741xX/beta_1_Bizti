package com.example.beta_1_Bizti.models;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="registros")
public class Registro {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name="fecha_registro", nullable=false)
  private LocalDateTime fechaRegistros;

  @Column(name="observacion", nullable=false, length = 200)
  private String observacion;

  @Column(name="estado", nullable=false, length = 50)
  private String estado;

  // --- RELACIÓN CON RETO ---
  @ManyToOne
  @JoinColumn(name = "id_reto")
  private Reto reto;

  // --- RELACIÓN CON USUARIO ---
  @ManyToOne
  @JoinColumn(name = "id_usuario")
  private Usuario usuario;

  // Constructor vacio
      public Registro() {
    }

  // --- GETTERS Y SETTERS ---

  public UUID getId() {
    return id;
  }
  public void setId(UUID id) {
    this.id = id;
  }
  public LocalDateTime getFechaRegistros() {
    return fechaRegistros;
  }
  public void setFechaRegistros(LocalDateTime fechaRegistros) {
    this.fechaRegistros = fechaRegistros;
  }
  public String getObservacion() {
    return observacion;
  }
  public void setObservacion(String observacion) {
    this.observacion = observacion;
  }
  public String getEstado() {
    return estado;
  }
  public void setEstado(String estado) {
    this.estado = estado;
  }
  public Reto getReto() {
    return reto;
  }
  public void setReto(Reto reto) {
    this.reto = reto;
  }
  public Usuario getUsuario() {
    return usuario;
  }
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
