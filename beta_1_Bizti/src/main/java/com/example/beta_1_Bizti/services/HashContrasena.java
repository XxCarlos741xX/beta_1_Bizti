package com.example.beta_1_Bizti.services;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

// Hash de contrasenas con PBKDF2 y sal aleatoria (formato "sal:hash", compatible con lo ya guardado)
@Component
public class HashContrasena {

    private static final int ITERACIONES = 65536;
    private static final int LONGITUD = 256;
    private final SecureRandom aleatorio = new SecureRandom();

    public String hashear(String contrasena) {
        byte[] sal = new byte[16];
        aleatorio.nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":" + Base64.getEncoder().encodeToString(derivar(contrasena, sal));
    }

    public boolean coincide(String contrasena, String guardado) {
        if (contrasena == null || guardado == null || !guardado.contains(":")) {
            return false;
        }
        String[] partes = guardado.split(":");
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        byte[] calculado = derivar(contrasena, Base64.getDecoder().decode(partes[0]));
        return MessageDigest.isEqual(esperado, calculado); // comparacion en tiempo constante
    }

    private byte[] derivar(String contrasena, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LONGITUD);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo cifrar la contrasena", e);
        }
    }
}
