package com.example.beta_1_Bizti.exception;

// 409: dato duplicado, registro en uso o regla de negocio incumplida
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
