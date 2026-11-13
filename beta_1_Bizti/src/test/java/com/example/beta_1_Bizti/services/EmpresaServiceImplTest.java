package com.example.beta_1_Bizti.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.beta_1_Bizti.dto.EmpresaRequestDto;
import com.example.beta_1_Bizti.dto.EmpresaResponseDto;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.exception.ReglaNegocioException;
import com.example.beta_1_Bizti.mapper.EmpresaMapper;
import com.example.beta_1_Bizti.models.Empresa;
import com.example.beta_1_Bizti.repository.IRepositorioEmpresa;
import com.example.beta_1_Bizti.repository.IRepositorioReto;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceImplTest {

    @Mock
    private IRepositorioEmpresa repositorio;

    @Mock
    private IRepositorioReto repositorioReto;

    private EmpresaServiceImpl servicio;

    private final EmpresaRequestDto dto = new EmpresaRequestDto(
            "Andina Tech", "901456781-2", "Tecnologia", "Mariana", "Alianzas@AndinaTech.co", "3004567812", true);

    @BeforeEach
    void preparar() {
        servicio = new EmpresaServiceImpl(repositorio, repositorioReto, new EmpresaMapper());
    }

    @Test
    void crearGuardaYDevuelveElDto() {
        when(repositorio.save(any(Empresa.class))).thenAnswer(i -> {
            Empresa e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });
        EmpresaResponseDto creada = servicio.crear(dto);
        assertEquals("Andina Tech", creada.nombre());
        assertEquals("alianzas@andinatech.co", creada.correo()); // el correo se normaliza
    }

    @Test
    void crearConNitRepetidoLanza409() {
        when(repositorio.existsByNit("901456781-2")).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> servicio.crear(dto));
        verify(repositorio, never()).save(any());
    }

    @Test
    void modificarInexistenteLanza404() {
        UUID id = UUID.randomUUID();
        when(repositorio.findById(id)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.modificar(id, dto));
    }

    @Test
    void eliminarConRetosLanza409() {
        UUID id = UUID.randomUUID();
        when(repositorio.findById(id)).thenReturn(Optional.of(new Empresa()));
        when(repositorioReto.existsByEmpresa_Id(id)).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> servicio.eliminar(id));
        verify(repositorio, never()).delete(any());
    }
}
