package com.example.beta_1_Bizti.config;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.beta_1_Bizti.exception.CredencialesInvalidasException;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.exception.ReglaNegocioException;

import jakarta.servlet.http.HttpServletRequest;

// Unico manejador de errores. Formato uniforme:
// { timestamp, status, error, message, path, errors: [ { campo, mensaje } ] }
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErrores.class);

    // 400: fallo la validacion de los DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<Map<String, String>> errores = e.getBindingResult().getFieldErrors().stream()
                .map(f -> Map.of("campo", f.getField(), "mensaje", String.valueOf(f.getDefaultMessage())))
                .toList();
        String resumen = errores.stream().map(x -> x.get("campo") + ": " + x.get("mensaje"))
                .collect(Collectors.joining("; "));
        return cuerpo(HttpStatus.BAD_REQUEST, "Datos invalidos. " + resumen, req, errores);
    }

    // 400: JSON mal formado o tipo incorrecto (texto donde va un numero, fecha invalida...)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.BAD_REQUEST,
                "El cuerpo de la peticion no es un JSON valido o algun campo tiene un tipo o formato incorrecto", req, List.of());
    }

    // 400: parametro de la URL con formato incorrecto (ej. id que no es UUID)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametro(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.BAD_REQUEST, "El parametro '" + e.getName() + "' no tiene un formato valido", req, List.of());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> credenciales(CredencialesInvalidasException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.UNAUTHORIZED, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.NOT_FOUND, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> rutaInexistente(NoResourceFoundException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.NOT_FOUND, "La ruta no existe", req, List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> metodo(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.METHOD_NOT_ALLOWED, "Metodo " + e.getMethod() + " no permitido en esta ruta", req, List.of());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(ReglaNegocioException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.CONFLICT, e.getMessage(), req, List.of());
    }

    // Respaldo: si la base rechaza algo que el servicio no valido
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException e, HttpServletRequest req) {
        return cuerpo(HttpStatus.CONFLICT, "Conflicto de datos: valor repetido o registro con elementos asociados", req, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> estado(ResponseStatusException e, HttpServletRequest req) {
        return cuerpo(e.getStatusCode(), e.getReason(), req, List.of());
    }

    // 500: error no controlado. Se registra en el log, pero nunca se envia el stack trace
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inesperado(Exception e, HttpServletRequest req) {
        // Las excepciones propias de Spring MVC (415, 406...) traen su propio codigo: no son 500
        if (e instanceof ErrorResponse respuesta) {
            return cuerpo(respuesta.getStatusCode(), respuesta.getBody().getDetail(), req, List.of());
        }
        LOG.error("Error no controlado en {} {}", req.getMethod(), req.getRequestURI(), e);
        return cuerpo(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req, List.of());
    }

    private ResponseEntity<Map<String, Object>> cuerpo(HttpStatusCode codigo, String mensaje,
            HttpServletRequest req, List<Map<String, String>> errores) {
        HttpStatus estado = HttpStatus.resolve(codigo.value());
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("timestamp", LocalDateTime.now().toString());
        json.put("status", codigo.value());
        json.put("error", estado != null ? estado.getReasonPhrase() : String.valueOf(codigo.value()));
        json.put("message", mensaje);
        json.put("path", req.getRequestURI());
        json.put("errors", errores);
        return ResponseEntity.status(codigo).body(json);
    }
}
