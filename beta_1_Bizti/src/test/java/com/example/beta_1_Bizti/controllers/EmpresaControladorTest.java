package com.example.beta_1_Bizti.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.beta_1_Bizti.dto.EmpresaRequestDto;
import com.example.beta_1_Bizti.dto.EmpresaResponseDto;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.services.EmpresaService;

@WebMvcTest(EmpresaControlador.class)
class EmpresaControladorTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EmpresaService servicio;

    private static final String VALIDA = """
            {"nombre":"Andina Tech","nit":"901456781-2","sector":"Tecnologia","contacto":"Mariana",
             "correo":"alianzas@andinatech.co","telefono":"3004567812","activa":true}""";

    @Test
    void postValidoResponde201ConLocation() throws Exception {
        UUID id = UUID.randomUUID();
        when(servicio.crear(any(EmpresaRequestDto.class))).thenReturn(new EmpresaResponseDto(
                id, "Andina Tech", "901456781-2", "Tecnologia", "Mariana", "alianzas@andinatech.co", "3004567812", true));
        mvc.perform(post("/api/v1/empresas").contentType(MediaType.APPLICATION_JSON).content(VALIDA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/empresas/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void postConCamposInvalidosResponde400ConLaLista() throws Exception {
        String invalida = """
                {"nit":"1","sector":"TI","contacto":"Ana","correo":"no-es-correo","telefono":"123","activa":true}""";
        mvc.perform(post("/api/v1/empresas").contentType(MediaType.APPLICATION_JSON).content(invalida))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(3)) // nombre, correo, telefono
                .andExpect(jsonPath("$.path").value("/api/v1/empresas"));
    }

    @Test
    void getInexistenteResponde404() throws Exception {
        UUID id = UUID.randomUUID();
        when(servicio.buscarPorId(eq(id))).thenThrow(new RecursoNoEncontradoException("Empresa no encontrada: " + id));
        mvc.perform(get("/api/v1/empresas/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Empresa no encontrada: " + id));
    }

    @Test
    void idQueNoEsUuidResponde400() throws Exception {
        mvc.perform(get("/api/v1/empresas/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void deleteResponde204() throws Exception {
        mvc.perform(delete("/api/v1/empresas/" + UUID.randomUUID())).andExpect(status().isNoContent());
    }

    @Test
    void deleteInexistenteResponde404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RecursoNoEncontradoException("Empresa no encontrada")).when(servicio).eliminar(id);
        mvc.perform(delete("/api/v1/empresas/" + id)).andExpect(status().isNotFound());
    }
}
