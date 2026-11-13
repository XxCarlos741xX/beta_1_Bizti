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

import com.example.beta_1_Bizti.dto.EmpresaRequestDto;
import com.example.beta_1_Bizti.dto.EmpresaResponseDto;
import com.example.beta_1_Bizti.services.EmpresaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/empresas")
@Tag(name = "empresas", description = "Empresas que publican retos")
public class EmpresaControlador {

    private final EmpresaService servicio;

    public EmpresaControlador(EmpresaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar (paginado): ?page=0&size=20&sort=nombre,asc")
    public Page<EmpresaResponseDto> listar(@PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return servicio.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por id")
    public EmpresaResponseDto buscar(@PathVariable UUID id) {
        return servicio.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear (201 + Location)")
    public ResponseEntity<EmpresaResponseDto> crear(@Valid @RequestBody EmpresaRequestDto dto) {
        EmpresaResponseDto creado = servicio.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar")
    public EmpresaResponseDto modificar(@PathVariable UUID id, @Valid @RequestBody EmpresaRequestDto dto) {
        return servicio.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (204)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
