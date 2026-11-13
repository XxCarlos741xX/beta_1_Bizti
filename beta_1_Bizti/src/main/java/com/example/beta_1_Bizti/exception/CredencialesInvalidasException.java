package com.example.beta_1_Bizti.exception;

// 401: correo o contrasena incorrectos
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
