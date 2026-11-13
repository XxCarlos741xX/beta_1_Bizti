package com.example.beta_1_Bizti.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beta_1_Bizti.models.Registro;
import com.example.beta_1_Bizti.services.ServiciosRegistros;

@RestController
@RequestMapping("/api/registros") 
public class RegistroControlador {

  @Autowired 
  ServiciosRegistros servicio;

  //Controlador para guardar
  @PostMapping 
  public ResponseEntity<?> guardar(@RequestBody Registro datos) {
    return ResponseEntity.status(HttpStatus.OK).body(servicio.guardarRegistro(datos));
  }

  //Controlador para buscar
  @GetMapping
  public ResponseEntity<?> buscar() {
    return ResponseEntity.status(HttpStatus.OK).body(servicio.buscar());
  }

  //Controlador para modificar
  @PutMapping("/{id}")
  public ResponseEntity<?> modificar(@PathVariable UUID id, @RequestBody Registro datos) {
    return ResponseEntity.status(HttpStatus.OK).body(servicio.modificar(id, datos));
  }


  //Controlador para eliminar
  @DeleteMapping("/{id}")
  public ResponseEntity<?> eliminar(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(servicio.eliminar(id));
  }
}
