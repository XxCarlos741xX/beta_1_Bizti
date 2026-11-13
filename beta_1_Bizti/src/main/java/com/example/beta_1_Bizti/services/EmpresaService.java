package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.beta_1_Bizti.dto.EmpresaRequestDto;
import com.example.beta_1_Bizti.dto.EmpresaResponseDto;

public interface EmpresaService {

    Page<EmpresaResponseDto> listar(Pageable pageable);

    EmpresaResponseDto buscarPorId(UUID id);

    EmpresaResponseDto crear(EmpresaRequestDto dto);

    EmpresaResponseDto modificar(UUID id, EmpresaRequestDto dto);

    void eliminar(UUID id);
}
