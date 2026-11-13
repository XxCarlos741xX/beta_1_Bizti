package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Datos de entrada para crear o modificar una empresa
public record EmpresaRequestDto(
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Size(max = 30) String nit,
        @NotBlank @Size(max = 100) String sector,
        @NotBlank @Size(max = 100) String contacto,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Pattern(regexp = "\\d{10}", message = "debe tener 10 dígitos") String telefono,
        @NotNull Boolean activa) {
}
