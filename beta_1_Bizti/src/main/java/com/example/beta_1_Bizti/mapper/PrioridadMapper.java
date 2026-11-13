package com.example.beta_1_Bizti.mapper;

import org.springframework.stereotype.Component;

import com.example.beta_1_Bizti.dto.PrioridadRequestDto;
import com.example.beta_1_Bizti.dto.PrioridadResponseDto;
import com.example.beta_1_Bizti.models.Prioridad;

@Component
public class PrioridadMapper {

    public void copiar(PrioridadRequestDto dto, Prioridad prioridad) {
        prioridad.setNombre(dto.nombre().trim());
        prioridad.setNivel(dto.nivel());
    }

    public PrioridadResponseDto aDto(Prioridad p) {
        return new PrioridadResponseDto(p.getId(), p.getNombre(), p.getNivel());
    }
}
