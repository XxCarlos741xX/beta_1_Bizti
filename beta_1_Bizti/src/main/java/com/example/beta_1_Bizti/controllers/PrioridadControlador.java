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

import com.example.beta_1_Bizti.models.Prioridad;
import com.example.beta_1_Bizti.services.ServiciosPrioridad;

@RestController
@RequestMapping ("/api/prioridades")
public class PrioridadControlador {

    @Autowired
    ServiciosPrioridad servicio;

    //Controlador para guardar
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Prioridad datos){
        return ResponseEntity.status(HttpStatus.OK).body(servicio.guardarPrioridad(datos));
    }

    //Controlador para buscar
    @GetMapping
    public ResponseEntity <?> buscar(){
        return ResponseEntity.status(HttpStatus.OK).body(servicio.listarPrioridades());
    }

    //Controlador para modificar
    @PutMapping ("/{id}")
    public ResponseEntity <?> modificar(@PathVariable UUID id, @RequestBody Prioridad datos){
        return ResponseEntity.status(HttpStatus.OK).body(servicio.modificarPrioridad(id, datos));
    }

    //Controlador para eliminar
    @DeleteMapping ("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(servicio.eliminarPrioridad(id));
    }

}
