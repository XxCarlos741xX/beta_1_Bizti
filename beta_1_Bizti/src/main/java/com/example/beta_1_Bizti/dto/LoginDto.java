package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Inicio de sesion
public record LoginDto(
        @NotBlank @Email String correo,
        @NotBlank String contrasena) {
}
