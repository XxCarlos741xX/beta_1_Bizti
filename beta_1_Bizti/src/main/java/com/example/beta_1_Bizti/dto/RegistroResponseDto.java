package com.example.beta_1_Bizti.dto;

import java.time.LocalDateTime;
import java.util.UUID;

// Registro que devuelve la API
public record RegistroResponseDto(
        UUID id,
        LocalDateTime fechaRegistros,
        String observacion,
        String estado,
        RefDto reto,
        RefDto usuario) {
}
