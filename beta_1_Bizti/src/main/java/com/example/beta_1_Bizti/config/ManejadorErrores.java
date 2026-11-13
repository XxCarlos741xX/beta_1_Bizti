package com.example.beta_1_Bizti.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// Respuestas de error en JSON con un mensaje legible para el frontend:
//  { "status": 404, "error": "Not Found", "message": "Empresa no encontrada: ..." }
@RestControllerAdvice
public class ManejadorErrores {

    // Errores de base de datos (dato repetido, registro en uso, campo obligatorio vacio) -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException e) {
        return cuerpo(HttpStatus.CONFLICT,
                "Conflicto de datos: valor repetido, campo obligatorio vacio o registro con elementos asociados");
    }

    // Errores lanzados por los servicios (404 no encontrado, 401 login, 400 contrasena corta...).
    // Se manejan aqui para que el mensaje siempre llegue al frontend, sin depender de
    // la propiedad server.error.include-message.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> estado(ResponseStatusException e) {
        String mensaje = e.getReason() != null ? e.getReason() : e.getMessage();
        return cuerpo(e.getStatusCode(), mensaje);
    }

    private ResponseEntity<Map<String, Object>> cuerpo(HttpStatusCode codigo, String mensaje) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("status", codigo.value());
        HttpStatus estado = HttpStatus.resolve(codigo.value());
        json.put("error", estado != null ? estado.getReasonPhrase() : String.valueOf(codigo.value()));
        json.put("message", mensaje);
        return ResponseEntity.status(codigo).body(json);
    }
}
