package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.beta_1_Bizti.dto.RegistroRequestDto;
import com.example.beta_1_Bizti.dto.RegistroResponseDto;

public interface RegistroService {

    Page<RegistroResponseDto> listar(Pageable pageable);

    RegistroResponseDto buscarPorId(UUID id);

    RegistroResponseDto crear(RegistroRequestDto dto);

    RegistroResponseDto modificar(UUID id, RegistroRequestDto dto);

    void eliminar(UUID id);
}
