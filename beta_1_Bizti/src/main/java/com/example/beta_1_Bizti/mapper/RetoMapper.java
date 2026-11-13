package com.example.beta_1_Bizti.mapper;

import org.springframework.stereotype.Component;

import com.example.beta_1_Bizti.dto.RefDto;
import com.example.beta_1_Bizti.dto.RetoResponseDto;
import com.example.beta_1_Bizti.models.Reto;

// Los datos de entrada se copian en el servicio, porque las relaciones se buscan en la base
@Component
public class RetoMapper {

    public RetoResponseDto aDto(Reto r) {
        return new RetoResponseDto(r.getId(), r.getNombre(), r.getDescripcion(), r.getFechaInicio(),
                r.getFechaFin(), r.getEstado(),
                r.getEmpresa() == null ? null : new RefDto(r.getEmpresa().getId(), r.getEmpresa().getNombre()),
                r.getCategoria() == null ? null : new RefDto(r.getCategoria().getId(), r.getCategoria().getNombre()),
                r.getPrioridad() == null ? null : new RefDto(r.getPrioridad().getId(), r.getPrioridad().getNombre()));
    }
}
