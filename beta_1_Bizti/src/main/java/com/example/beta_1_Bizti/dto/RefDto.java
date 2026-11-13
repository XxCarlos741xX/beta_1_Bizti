package com.example.beta_1_Bizti.dto;

import java.util.UUID;

// Relacion de salida: { "id": "...", "nombre": "..." }
public record RefDto(
        UUID id,
        String nombre) {
}
