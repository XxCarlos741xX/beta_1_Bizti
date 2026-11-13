package com.example.beta_1_Bizti.mapper;

import org.springframework.stereotype.Component;

import com.example.beta_1_Bizti.dto.UsuarioResponseDto;
import com.example.beta_1_Bizti.models.Usuario;

// La contrasena nunca se copia al DTO de salida
@Component
public class UsuarioMapper {

    public UsuarioResponseDto aDto(Usuario u) {
        return new UsuarioResponseDto(u.getId(), u.getNombre(), u.getCorreo(), u.getRol());
    }
}
