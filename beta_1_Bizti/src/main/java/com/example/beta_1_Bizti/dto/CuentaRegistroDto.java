package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Registro publico de una cuenta (siempre con rol usuario)
public record CuentaRegistroDto(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Size(min = 6, max = 72) String contrasena) {
}
