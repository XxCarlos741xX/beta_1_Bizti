package com.example.beta_1_Bizti.dto;

import java.util.UUID;

// Categoria que devuelve la API
public record CategoriaResponseDto(
        UUID id,
        String nombre,
        String descripcion) {
}
