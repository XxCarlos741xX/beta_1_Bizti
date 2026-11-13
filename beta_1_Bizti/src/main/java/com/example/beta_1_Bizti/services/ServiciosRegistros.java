package com.example.beta_1_Bizti.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.beta_1_Bizti.models.Registro;
import com.example.beta_1_Bizti.models.Reto;
import com.example.beta_1_Bizti.models.Usuario;
import com.example.beta_1_Bizti.repository.IRepositorioUsuario;
import com.example.beta_1_Bizti.repository.IReporsitorioReto;
import com.example.beta_1_Bizti.repository.IRepositorioRegistro;

@Service 
public class ServiciosRegistros {

@Autowired 
private IRepositorioRegistro repositorioRegistro;

@Autowired
private IReporsitorioReto repositorioReto;

@Autowired
private IRepositorioUsuario repositorioUsuario;

// Busca el reto completo cuando el JSON solo trae su id: "reto": {"id": "..."}
private void asignarReto(Registro registro, Registro datos) {
  Reto reto = null;
  if (datos.getReto() != null && datos.getReto().getId() != null) {
    UUID idReto = datos.getReto().getId();
    reto = repositorioReto.findById(idReto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reto no encontrado: " + idReto));
  }
  registro.setReto(reto);

  Usuario usuario = null;
  if (datos.getUsuario() != null && datos.getUsuario().getId() != null) {
    UUID idUsuario = datos.getUsuario().getId();
    usuario = repositorioUsuario.findById(idUsuario)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + idUsuario));
  }
  registro.setUsuario(usuario);
}
  
//Operaciones
//Guardar
public Registro guardarRegistro(Registro datosRegistro) {
  asignarReto(datosRegistro, datosRegistro);
  return this.repositorioRegistro.save(datosRegistro);

}

public List<Registro> buscar() {
  return this.repositorioRegistro.findAll();
}

public Registro modificar(UUID id, Registro datosNuevos) {

  Optional<Registro> registroBuscado = this.repositorioRegistro.findById(id);
  if (registroBuscado.isPresent()) {
    //Hay a quien actualizar
    Registro registroEncontrado = registroBuscado.get();

    //Modificando los datos
    registroEncontrado.setFechaRegistros(datosNuevos.getFechaRegistros());
    registroEncontrado.setObservacion(datosNuevos.getObservacion());
    registroEncontrado.setEstado(datosNuevos.getEstado());
    asignarReto(registroEncontrado, datosNuevos);

    // Guardo los datos

    return this.repositorioRegistro.save(registroEncontrado);

  } else {
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado");
  }
}


  public boolean eliminar(UUID id){
    Optional<Registro> registroBuscado = this.repositorioRegistro.findById(id);
    if (registroBuscado.isPresent()) {
      this.repositorioRegistro.deleteById(id);
      return true;
    }else{
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado");
    }
  }


}
