package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Datos de entrada de una prioridad (nivel 1 = la mas alta)
public record PrioridadRequestDto(
        @NotBlank @Size(max = 255) String nombre,
        @NotNull @Min(1) Integer nivel) {
}
