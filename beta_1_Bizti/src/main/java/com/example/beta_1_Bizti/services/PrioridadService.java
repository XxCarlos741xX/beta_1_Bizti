package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.beta_1_Bizti.dto.PrioridadRequestDto;
import com.example.beta_1_Bizti.dto.PrioridadResponseDto;

public interface PrioridadService {

    Page<PrioridadResponseDto> listar(Pageable pageable);

    PrioridadResponseDto buscarPorId(UUID id);

    PrioridadResponseDto crear(PrioridadRequestDto dto);

    PrioridadResponseDto modificar(UUID id, PrioridadRequestDto dto);

    void eliminar(UUID id);
}
