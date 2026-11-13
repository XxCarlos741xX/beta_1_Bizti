package com.example.beta_1_Bizti.mapper;

import org.springframework.stereotype.Component;

import com.example.beta_1_Bizti.dto.RefDto;
import com.example.beta_1_Bizti.dto.RegistroResponseDto;
import com.example.beta_1_Bizti.models.Registro;

@Component
public class RegistroMapper {

    public RegistroResponseDto aDto(Registro r) {
        return new RegistroResponseDto(r.getId(), r.getFechaRegistro(), r.getObservacion(), r.getEstado(),
                r.getReto() == null ? null : new RefDto(r.getReto().getId(), r.getReto().getNombre()),
                r.getUsuario() == null ? null : new RefDto(r.getUsuario().getId(), r.getUsuario().getNombre()));
    }
}
