package com.example.beta_1_Bizti.dto;

import java.time.LocalDate;
import java.util.UUID;

// Reto que devuelve la API
public record RetoResponseDto(
        UUID id,
        String nombre,
        String descripcion,
        LocalDate fecha_inicio,
        LocalDate fecha_fin,
        String estado,
        RefDto empresa,
        RefDto categoria,
        RefDto prioridad) {
}
