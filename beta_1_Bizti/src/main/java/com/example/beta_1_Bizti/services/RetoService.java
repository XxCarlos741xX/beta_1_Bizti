package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.beta_1_Bizti.dto.RetoRequestDto;
import com.example.beta_1_Bizti.dto.RetoResponseDto;

public interface RetoService {

    Page<RetoResponseDto> listar(Pageable pageable);

    RetoResponseDto buscarPorId(UUID id);

    RetoResponseDto crear(RetoRequestDto dto);

    RetoResponseDto modificar(UUID id, RetoRequestDto dto);

    void eliminar(UUID id);
}
