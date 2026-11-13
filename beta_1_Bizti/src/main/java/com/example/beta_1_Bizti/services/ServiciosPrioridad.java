package com.example.beta_1_Bizti.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.beta_1_Bizti.models.Prioridad;
import com.example.beta_1_Bizti.repository.IRepositorioPrioridad;

@Service
public class ServiciosPrioridad {
    private final IRepositorioPrioridad repositorioPrioridad;

    public ServiciosPrioridad(IRepositorioPrioridad repositorioPrioridad) {
        this.repositorioPrioridad = repositorioPrioridad;
    }

    // Crear
    public Prioridad guardarPrioridad(Prioridad datosPrioridad) {
        return this.repositorioPrioridad.save(datosPrioridad);
    }

    // Leer por id
    public Optional<Prioridad> buscarPrioridadPorId(UUID id) {
        return this.repositorioPrioridad.findById(id);
    }

    // Listar todos
    public List<Prioridad> listarPrioridades() {
        return this.repositorioPrioridad.findAll();
    }

    // Modificar
    public Prioridad modificarPrioridad(UUID id, Prioridad datosNuevos) {
        Optional<Prioridad> prioridadBuscado = this.repositorioPrioridad.findById(id);
        if (prioridadBuscado.isPresent()) {
            Prioridad prioridadEncontrado = prioridadBuscado.get();
            prioridadEncontrado.setNombre(datosNuevos.getNombre());
            prioridadEncontrado.setNivel(datosNuevos.getNivel());
            return this.repositorioPrioridad.save(prioridadEncontrado);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la prioridad");
        }
    }

    // Eliminar
    public boolean eliminarPrioridad(UUID id) {
        Optional <Prioridad> prioridadBuscado=this.repositorioPrioridad.findById(id);
        if (prioridadBuscado.isPresent()) {
            this.repositorioPrioridad.deleteById(id);
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la prioridad");
        }
    }
}
