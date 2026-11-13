package com.example.beta_1_Bizti.mapper;

import org.springframework.stereotype.Component;

import com.example.beta_1_Bizti.dto.CategoriaRequestDto;
import com.example.beta_1_Bizti.dto.CategoriaResponseDto;
import com.example.beta_1_Bizti.models.Categoria;

@Component
public class CategoriaMapper {

    public void copiar(CategoriaRequestDto dto, Categoria categoria) {
        categoria.setNombre(dto.nombre().trim());
        categoria.setDescripcion(dto.descripcion().trim());
    }

    public CategoriaResponseDto aDto(Categoria c) {
        return new CategoriaResponseDto(c.getId(), c.getNombre(), c.getDescripcion());
    }
}
