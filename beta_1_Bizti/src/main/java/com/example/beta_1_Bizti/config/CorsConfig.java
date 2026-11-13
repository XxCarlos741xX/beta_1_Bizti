package com.example.beta_1_Bizti.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS explicito: los origenes vienen de bizti.cors.origenes (por defecto, localhost en cualquier puerto).
// En produccion se cambia por el dominio real del frontend, nunca "*".
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origenes;

    public CorsConfig(@Value("${bizti.cors.origenes:http://localhost:*,http://127.0.0.1:*}") String[] origenes) {
        this.origenes = origenes;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(origenes)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type")
                .exposedHeaders("Location")
                .maxAge(3600);
    }
}
