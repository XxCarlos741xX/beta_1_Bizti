package com.example.beta_1_Bizti.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.beta_1_Bizti.models.Empresa;
import com.example.beta_1_Bizti.repository.iRepositorioEmpresa;

@Service 
public class ServiciosEmpresa {

@Autowired 
private iRepositorioEmpresa repositorioEmpresa;

//Guardar
public Empresa guardarEmpresa(Empresa datosEmpresa){
     return this.repositorioEmpresa.save(datosEmpresa);
    
 }

public List<Empresa> buscar(){
    return this.repositorioEmpresa.findAll();
     
}

public Empresa modificar(UUID id, Empresa datosNuevos){

    Optional<Empresa> empresaBuscado=this.repositorioEmpresa.findById(id);
    if(empresaBuscado.isPresent()){
        //Hay a quien actualizar
        Empresa empresaEncontrado=empresaBuscado.get();

        //Modificando los datos
        empresaEncontrado.setNombre(datosNuevos.getNombre());
        empresaEncontrado.setNit(datosNuevos.getNit());
        empresaEncontrado.setSector(datosNuevos.getSector());
        empresaEncontrado.setContacto(datosNuevos.getContacto());
        empresaEncontrado.setCorreo(datosNuevos.getCorreo());
        empresaEncontrado.setTelefono(datosNuevos.getTelefono());
        empresaEncontrado.setActiva(datosNuevos.getActiva());

        //Guardo los cambios
        return
         this.repositorioEmpresa.save(empresaEncontrado);

    }else{
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Empresa no encontrada");
    }

}

public boolean eliminar(UUID id){
    Optional<Empresa> empresaBuscado=this.repositorioEmpresa.findById(id);
    if (empresaBuscado.isPresent()) {
        
        this.repositorioEmpresa.deleteById(id);
        return true;

    }else{
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No se encontro la empresa");
    }
}

}
