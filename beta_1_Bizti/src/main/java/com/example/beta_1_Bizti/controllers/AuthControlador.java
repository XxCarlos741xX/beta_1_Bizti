package com.example.beta_1_Bizti.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.beta_1_Bizti.dto.CuentaRegistroDto;
import com.example.beta_1_Bizti.dto.LoginDto;
import com.example.beta_1_Bizti.dto.UsuarioResponseDto;
import com.example.beta_1_Bizti.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Registro y login. Devuelve el usuario (sin contrasena); no emite token.
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "auth", description = "Registro e inicio de sesion")
public class AuthControlador {

    private final UsuarioService servicio;

    public AuthControlador(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/registro")
    @Operation(summary = "Crear una cuenta (rol usuario)")
    public ResponseEntity<UsuarioResponseDto> registro(@Valid @RequestBody CuentaRegistroDto dto) {
        UsuarioResponseDto creado = servicio.registrar(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/usuarios/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion (401 si las credenciales no coinciden)")
    public UsuarioResponseDto login(@Valid @RequestBody LoginDto dto) {
        return servicio.login(dto);
    }
}
