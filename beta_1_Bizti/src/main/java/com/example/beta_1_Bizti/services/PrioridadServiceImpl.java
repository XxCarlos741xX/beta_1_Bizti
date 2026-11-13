package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beta_1_Bizti.dto.PrioridadRequestDto;
import com.example.beta_1_Bizti.dto.PrioridadResponseDto;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.exception.ReglaNegocioException;
import com.example.beta_1_Bizti.mapper.PrioridadMapper;
import com.example.beta_1_Bizti.models.Prioridad;
import com.example.beta_1_Bizti.repository.IRepositorioPrioridad;
import com.example.beta_1_Bizti.repository.IRepositorioReto;

@Service
@Transactional
public class PrioridadServiceImpl implements PrioridadService {

    private final IRepositorioPrioridad repositorio;
    private final IRepositorioReto repositorioReto;
    private final PrioridadMapper mapper;

    public PrioridadServiceImpl(IRepositorioPrioridad repositorio, IRepositorioReto repositorioReto, PrioridadMapper mapper) {
        this.repositorio = repositorio;
        this.repositorioReto = repositorioReto;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrioridadResponseDto> listar(Pageable pageable) {
        return repositorio.findAll(pageable).map(mapper::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PrioridadResponseDto buscarPorId(UUID id) {
        return mapper.aDto(obtener(id));
    }

    @Override
    public PrioridadResponseDto crear(PrioridadRequestDto dto) {
        Prioridad prioridad = new Prioridad();
        mapper.copiar(dto, prioridad);
        return mapper.aDto(repositorio.save(prioridad));
    }

    @Override
    public PrioridadResponseDto modificar(UUID id, PrioridadRequestDto dto) {
        Prioridad prioridad = obtener(id);
        mapper.copiar(dto, prioridad);
        return mapper.aDto(repositorio.save(prioridad));
    }

    @Override
    public void eliminar(UUID id) {
        Prioridad prioridad = obtener(id);
        if (repositorioReto.existsByPrioridad_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar la prioridad: tiene retos asociados");
        }
        repositorio.delete(prioridad);
    }

    private Prioridad obtener(UUID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Prioridad no encontrada: " + id));
    }
}
