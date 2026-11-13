package com.example.beta_1_Bizti.dto;

import java.util.UUID;

// Prioridad que devuelve la API
public record PrioridadResponseDto(
        UUID id,
        String nombre,
        Integer nivel) {
}
