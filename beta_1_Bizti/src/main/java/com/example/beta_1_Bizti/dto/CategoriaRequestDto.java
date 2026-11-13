package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos de entrada de una categoria
public record CategoriaRequestDto(
        @NotBlank @Size(max = 255) String nombre,
        @NotBlank @Size(max = 255) String descripcion) {
}
