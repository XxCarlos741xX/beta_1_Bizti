package com.example.beta_1_Bizti.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.beta_1_Bizti.models.Categoria;
import com.example.beta_1_Bizti.models.Empresa;
import com.example.beta_1_Bizti.models.Prioridad;
import com.example.beta_1_Bizti.models.Reto;
import com.example.beta_1_Bizti.repository.IReporsitorioReto;
import com.example.beta_1_Bizti.repository.IRepositorioCategoria;
import com.example.beta_1_Bizti.repository.IRepositorioPrioridad;
import com.example.beta_1_Bizti.repository.iRepositorioEmpresa;

@Service
public class ServiciosReto {

    @Autowired
    private IReporsitorioReto reporsitorioReto;

    @Autowired
    private iRepositorioEmpresa repositorioEmpresa;

    @Autowired
    private IRepositorioCategoria repositorioCategoria;

    @Autowired
    private IRepositorioPrioridad repositorioPrioridad;

    // Busca en la base de datos la empresa, categoria y prioridad que llegan solo
    // con el id en el JSON, por ejemplo: "empresa": {"id": "..."}
    private void asignarRelaciones(Reto reto, Reto datos) {
        Empresa empresa = null;
        if (datos.getEmpresa() != null && datos.getEmpresa().getId() != null) {
            UUID id = datos.getEmpresa().getId();
            empresa = repositorioEmpresa.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada: " + id));
        }
        Categoria categoria = null;
        if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
            UUID id = datos.getCategoria().getId();
            categoria = repositorioCategoria.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada: " + id));
        }
        Prioridad prioridad = null;
        if (datos.getPrioridad() != null && datos.getPrioridad().getId() != null) {
            UUID id = datos.getPrioridad().getId();
            prioridad = repositorioPrioridad.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prioridad no encontrada: " + id));
        }
        reto.setEmpresa(empresa);
        reto.setCategoria(categoria);
        reto.setPrioridad(prioridad);
    }

    // Guardar
    public Reto guardaReto(Reto datoReto) {
        asignarRelaciones(datoReto, datoReto);
        return this.reporsitorioReto.save(datoReto);
    }

    public List<Reto> buscar() {
        return this.reporsitorioReto.findAll();
    }

    public Reto modificar(UUID id, Reto datosNuevos) {
        Reto retoencontrado = this.reporsitorioReto.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reto no encontrado"));

        // Modificando los Datos
        retoencontrado.setNombre(datosNuevos.getNombre());
        retoencontrado.setDescripcion(datosNuevos.getDescripcion());
        retoencontrado.setFecha_inicio(datosNuevos.getFecha_inicio());
        retoencontrado.setFecha_fin(datosNuevos.getFecha_fin());
        retoencontrado.setEstado(datosNuevos.getEstado());
        asignarRelaciones(retoencontrado, datosNuevos);

        // Guardar los Cambios
        return this.reporsitorioReto.save(retoencontrado);
    }

    public boolean eliminar(UUID id) {
        if (!this.reporsitorioReto.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reto no encontrado");
        }
        this.reporsitorioReto.deleteById(id);
        return true;
    }

}
