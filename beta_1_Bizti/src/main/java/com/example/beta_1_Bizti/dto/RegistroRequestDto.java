package com.example.beta_1_Bizti.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

// Inscripcion de un usuario a un reto
public record RegistroRequestDto(
        @NotNull LocalDateTime fechaRegistros,
        @NotNull @Size(max = 200) String observacion,
        @NotBlank @Size(max = 50) String estado,
        @Valid RefIdDto reto,
        @Valid RefIdDto usuario) {
}
