package com.example.beta_1_Bizti.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.beta_1_Bizti.dto.UsuarioCrearDto;
import com.example.beta_1_Bizti.dto.UsuarioModificarDto;
import com.example.beta_1_Bizti.dto.UsuarioResponseDto;
import com.example.beta_1_Bizti.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "usuarios", description = "Usuarios de la plataforma")
public class UsuarioControlador {

    private final UsuarioService servicio;

    public UsuarioControlador(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar (paginado): ?page=0&size=20&sort=nombre,asc")
    public Page<UsuarioResponseDto> listar(@PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return servicio.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por id")
    public UsuarioResponseDto buscar(@PathVariable UUID id) {
        return servicio.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear (201 + Location)")
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody UsuarioCrearDto dto) {
        UsuarioResponseDto creado = servicio.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar")
    public UsuarioResponseDto modificar(@PathVariable UUID id, @Valid @RequestBody UsuarioModificarDto dto) {
        return servicio.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (204)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
