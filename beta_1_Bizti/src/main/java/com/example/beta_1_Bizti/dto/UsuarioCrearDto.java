package com.example.beta_1_Bizti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Crear un usuario desde el CRUD (si no se envia rol, queda como usuario)
public record UsuarioCrearDto(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Size(min = 6, max = 72) String contrasena,
        @Pattern(regexp = "usuario|evaluador|administrador", message = "debe ser usuario, evaluador o administrador") String rol) {
}
