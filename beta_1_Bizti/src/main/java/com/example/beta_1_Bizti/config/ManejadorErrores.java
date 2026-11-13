package com.example.beta_1_Bizti.config;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Convierte los errores de base de datos (dato repetido, registro en uso,
// campo obligatorio vacio) en una respuesta 409 con un mensaje legible,
// en lugar de un error 500 sin explicacion.
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridad(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Conflicto de datos: valor repetido, campo obligatorio vacio o registro con elementos asociados"));
    }
}
