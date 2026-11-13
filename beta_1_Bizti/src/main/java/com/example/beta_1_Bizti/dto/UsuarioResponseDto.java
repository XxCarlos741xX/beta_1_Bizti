package com.example.beta_1_Bizti.dto;

import java.util.UUID;

// Usuario que devuelve la API (sin contrasena)
public record UsuarioResponseDto(
        UUID id,
        String nombre,
        String correo,
        String rol) {
}
