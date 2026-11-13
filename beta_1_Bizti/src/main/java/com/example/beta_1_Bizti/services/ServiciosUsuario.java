package com.example.beta_1_Bizti.services;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.beta_1_Bizti.models.Usuario;
import com.example.beta_1_Bizti.repository.IRepositorioUsuario;

@Service
public class ServiciosUsuario {

    @Autowired
    private IRepositorioUsuario repositorio;

    // ---- Contraseñas: PBKDF2 con sal aleatoria (solo clases del JDK) ----
    private static String hashear(String contrasena) {
        try {
            byte[] sal = new byte[16];
            new SecureRandom().nextBytes(sal);
            return Base64.getEncoder().encodeToString(sal) + ":" + derivar(contrasena, sal);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cifrar la contrasena", e);
        }
    }

    private static String derivar(String contrasena, byte[] sal) throws Exception {
        KeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, 65536, 256);
        byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    private static boolean coincide(String contrasena, String guardado) {
        try {
            String[] partes = guardado.split(":");
            return derivar(contrasena, Base64.getDecoder().decode(partes[0])).equals(partes[1]);
        } catch (Exception e) {
            return false;
        }
    }

    private static void exigirContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contrasena debe tener al menos 6 caracteres");
        }
    }

    // ---- CRUD ----
    public Usuario guardar(Usuario datos) {
        exigirContrasena(datos.getContrasena());
        if (datos.getRol() == null || datos.getRol().isBlank()) {
            datos.setRol("usuario");
        }
        datos.setContrasena(hashear(datos.getContrasena()));
        return repositorio.save(datos);
    }

    public List<Usuario> buscar() {
        return repositorio.findAll();
    }

    public Usuario modificar(UUID id, Usuario nuevos) {
        Usuario u = repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        u.setNombre(nuevos.getNombre());
        u.setCorreo(nuevos.getCorreo());
        u.setRol(nuevos.getRol());
        // Si no se envia contrasena, se conserva la actual
        if (nuevos.getContrasena() != null && !nuevos.getContrasena().isBlank()) {
            exigirContrasena(nuevos.getContrasena());
            u.setContrasena(hashear(nuevos.getContrasena()));
        }
        return repositorio.save(u);
    }

    public boolean eliminar(UUID id) {
        if (!repositorio.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        repositorio.deleteById(id);
        return true;
    }

    // ---- Login ----
    public Usuario login(String correo, String contrasena) {
        Usuario u = repositorio.findByCorreo(correo == null ? "" : correo.trim()).orElse(null);
        if (u == null || contrasena == null || !coincide(contrasena, u.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contrasena incorrectos");
        }
        return u;
    }
}
