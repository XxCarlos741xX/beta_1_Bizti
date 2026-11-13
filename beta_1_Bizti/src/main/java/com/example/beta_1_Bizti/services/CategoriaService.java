package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.beta_1_Bizti.dto.CategoriaRequestDto;
import com.example.beta_1_Bizti.dto.CategoriaResponseDto;

public interface CategoriaService {

    Page<CategoriaResponseDto> listar(Pageable pageable);

    CategoriaResponseDto buscarPorId(UUID id);

    CategoriaResponseDto crear(CategoriaRequestDto dto);

    CategoriaResponseDto modificar(UUID id, CategoriaRequestDto dto);

    void eliminar(UUID id);
}
