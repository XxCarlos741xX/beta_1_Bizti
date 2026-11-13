package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.beta_1_Bizti.dto.UsuarioCrearDto;
import com.example.beta_1_Bizti.dto.UsuarioResponseDto;
import com.example.beta_1_Bizti.dto.UsuarioModificarDto;
import com.example.beta_1_Bizti.dto.CuentaRegistroDto;
import com.example.beta_1_Bizti.dto.LoginDto;

public interface UsuarioService {

    Page<UsuarioResponseDto> listar(Pageable pageable);

    UsuarioResponseDto buscarPorId(UUID id);

    UsuarioResponseDto crear(UsuarioCrearDto dto);

    UsuarioResponseDto modificar(UUID id, UsuarioModificarDto dto);

    void eliminar(UUID id);

    // Registro publico: siempre crea con rol usuario
    UsuarioResponseDto registrar(CuentaRegistroDto dto);

    UsuarioResponseDto login(LoginDto dto);
}
