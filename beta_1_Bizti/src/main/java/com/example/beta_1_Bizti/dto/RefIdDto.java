package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

// Relacion de entrada: { "id": "..." }
public record RefIdDto(
        @NotNull UUID id) {
}
