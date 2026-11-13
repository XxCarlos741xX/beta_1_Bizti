package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beta_1_Bizti.dto.RefIdDto;
import com.example.beta_1_Bizti.dto.RetoRequestDto;
import com.example.beta_1_Bizti.dto.RetoResponseDto;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.exception.ReglaNegocioException;
import com.example.beta_1_Bizti.mapper.RetoMapper;
import com.example.beta_1_Bizti.models.Reto;
import com.example.beta_1_Bizti.repository.IRepositorioCategoria;
import com.example.beta_1_Bizti.repository.IRepositorioEmpresa;
import com.example.beta_1_Bizti.repository.IRepositorioPrioridad;
import com.example.beta_1_Bizti.repository.IRepositorioRegistro;
import com.example.beta_1_Bizti.repository.IRepositorioReto;

@Service
@Transactional
public class RetoServiceImpl implements RetoService {

    private final IRepositorioReto repositorio;
    private final IRepositorioEmpresa repositorioEmpresa;
    private final IRepositorioCategoria repositorioCategoria;
    private final IRepositorioPrioridad repositorioPrioridad;
    private final IRepositorioRegistro repositorioRegistro;
    private final RetoMapper mapper;

    public RetoServiceImpl(IRepositorioReto repositorio, IRepositorioEmpresa repositorioEmpresa,
            IRepositorioCategoria repositorioCategoria, IRepositorioPrioridad repositorioPrioridad,
            IRepositorioRegistro repositorioRegistro, RetoMapper mapper) {
        this.repositorio = repositorio;
        this.repositorioEmpresa = repositorioEmpresa;
        this.repositorioCategoria = repositorioCategoria;
        this.repositorioPrioridad = repositorioPrioridad;
        this.repositorioRegistro = repositorioRegistro;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RetoResponseDto> listar(Pageable pageable) {
        return repositorio.findAll(pageable).map(mapper::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RetoResponseDto buscarPorId(UUID id) {
        return mapper.aDto(obtener(id));
    }

    @Override
    public RetoResponseDto crear(RetoRequestDto dto) {
        Reto reto = new Reto();
        copiar(dto, reto);
        return mapper.aDto(repositorio.save(reto));
    }

    @Override
    public RetoResponseDto modificar(UUID id, RetoRequestDto dto) {
        Reto reto = obtener(id);
        copiar(dto, reto);
        return mapper.aDto(repositorio.save(reto));
    }

    @Override
    public void eliminar(UUID id) {
        Reto reto = obtener(id);
        if (repositorioRegistro.existsByReto_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar el reto: tiene registros asociados");
        }
        repositorio.delete(reto);
    }

    private Reto obtener(UUID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reto no encontrado: " + id));
    }

    // Copia los datos y busca en la base las relaciones que llegan solo con su id
    private void copiar(RetoRequestDto dto, Reto reto) {
        if (dto.fecha_fin().isBefore(dto.fecha_inicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        reto.setNombre(dto.nombre().trim());
        reto.setDescripcion(dto.descripcion());
        reto.setFechaInicio(dto.fecha_inicio());
        reto.setFechaFin(dto.fecha_fin());
        reto.setEstado(dto.estado().trim());
        reto.setEmpresa(id(dto.empresa()) == null ? null : repositorioEmpresa.findById(id(dto.empresa()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada: " + id(dto.empresa()))));
        reto.setCategoria(id(dto.categoria()) == null ? null : repositorioCategoria.findById(id(dto.categoria()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada: " + id(dto.categoria()))));
        reto.setPrioridad(id(dto.prioridad()) == null ? null : repositorioPrioridad.findById(id(dto.prioridad()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Prioridad no encontrada: " + id(dto.prioridad()))));
    }

    private static UUID id(RefIdDto ref) {
        return ref == null ? null : ref.id();
    }
}
