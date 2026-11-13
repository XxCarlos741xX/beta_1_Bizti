package com.example.beta_1_Bizti.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// Datos de entrada de un reto. Las relaciones son opcionales y se envian como { "id": "..." }
public record RetoRequestDto(
        @NotBlank @Size(max = 50) String nombre,
        @Size(max = 200) String descripcion,
        @NotNull LocalDate fecha_inicio,
        @NotNull LocalDate fecha_fin,
        @NotBlank @Size(max = 50) String estado,
        @Valid RefIdDto empresa,
        @Valid RefIdDto categoria,
        @Valid RefIdDto prioridad) {
}
