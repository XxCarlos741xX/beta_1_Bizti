package com.example.beta_1_Bizti.dto;

import java.util.UUID;

// Empresa que devuelve la API
public record EmpresaResponseDto(
        UUID id,
        String nombre,
        String nit,
        String sector,
        String contacto,
        String correo,
        String telefono,
        Boolean activa) {
}
