package com.example.beta_1_Bizti.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beta_1_Bizti.models.Usuario;
import com.example.beta_1_Bizti.services.ServiciosUsuario;

// Registro y login. Devuelve el usuario (sin contrasena); no emite token.
@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    @Autowired
    ServiciosUsuario servicio;

    // Cuerpo: { "nombre": "...", "correo": "...", "contrasena": "..." }
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Usuario datos) {
        datos.setRol("usuario"); // el auto-registro nunca crea administradores
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.guardar(datos));
    }

    // Cuerpo: { "correo": "...", "contrasena": "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos) {
        return ResponseEntity.ok(servicio.login(datos.get("correo"), datos.get("contrasena")));
    }
}
