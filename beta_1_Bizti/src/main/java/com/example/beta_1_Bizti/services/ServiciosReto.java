package com.example.beta_1_Bizti.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.beta_1_Bizti.models.Reto;
import com.example.beta_1_Bizti.repository.IReporsitorioReto;

@Service 
public class ServiciosReto {

    @Autowired 
    private IReporsitorioReto reporsitorioReto;

    //Guardar
    public Reto guardaReto(Reto datoReto){
        return this.reporsitorioReto.save(datoReto);
    }

    public List<Reto> buscar(){
        return this.reporsitorioReto.findAll();
    }

    public Reto modificar(UUID id,Reto datosNuevos){

        Optional<Reto> retoBuscado=this.reporsitorioReto.findById(id);
        if(retoBuscado.isPresent()){
            //Hay a quien actualizar
            Reto retoencontrado = retoBuscado.get();

            //Modificando los Datos
            retoencontrado.setNombre(datosNuevos.getNombre());
            retoencontrado.setDescripcion(datosNuevos.getDescripcion());

            //Guardar los Cambios
            return this.reporsitorioReto.save(retoencontrado);

        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Reto no encontrado");
        }

    }

    public boolean eliminar(UUID id){
        Optional<Reto> retoBuscado=this.reporsitorioReto.findById(id);
        if(retoBuscado.isPresent()){
                this.reporsitorioReto.deleteById(id);
                return true;
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Reto no encontrado");
        }
    }
    
}
