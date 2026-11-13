package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Modificar un usuario: si la contrasena viene vacia, se conserva la actual
public record UsuarioModificarDto(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Pattern(regexp = "usuario|evaluador|administrador", message = "debe ser usuario, evaluador o administrador") String rol,
        @Size(min = 6, max = 72) String contrasena) {
}
