package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beta_1_Bizti.dto.RefIdDto;
import com.example.beta_1_Bizti.dto.RegistroRequestDto;
import com.example.beta_1_Bizti.dto.RegistroResponseDto;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.mapper.RegistroMapper;
import com.example.beta_1_Bizti.models.Registro;
import com.example.beta_1_Bizti.repository.IRepositorioRegistro;
import com.example.beta_1_Bizti.repository.IRepositorioReto;
import com.example.beta_1_Bizti.repository.IRepositorioUsuario;

@Service
@Transactional
public class RegistroServiceImpl implements RegistroService {

    private final IRepositorioRegistro repositorio;
    private final IRepositorioReto repositorioReto;
    private final IRepositorioUsuario repositorioUsuario;
    private final RegistroMapper mapper;

    public RegistroServiceImpl(IRepositorioRegistro repositorio, IRepositorioReto repositorioReto,
            IRepositorioUsuario repositorioUsuario, RegistroMapper mapper) {
        this.repositorio = repositorio;
        this.repositorioReto = repositorioReto;
        this.repositorioUsuario = repositorioUsuario;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroResponseDto> listar(Pageable pageable) {
        return repositorio.findAll(pageable).map(mapper::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroResponseDto buscarPorId(UUID id) {
        return mapper.aDto(obtener(id));
    }

    @Override
    public RegistroResponseDto crear(RegistroRequestDto dto) {
        Registro registro = new Registro();
        copiar(dto, registro);
        return mapper.aDto(repositorio.save(registro));
    }

    @Override
    public RegistroResponseDto modificar(UUID id, RegistroRequestDto dto) {
        Registro registro = obtener(id);
        copiar(dto, registro);
        return mapper.aDto(repositorio.save(registro));
    }

    @Override
    public void eliminar(UUID id) {
        repositorio.delete(obtener(id));
    }

    private Registro obtener(UUID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro no encontrado: " + id));
    }

    private void copiar(RegistroRequestDto dto, Registro registro) {
        registro.setFechaRegistro(dto.fechaRegistros());
        registro.setObservacion(dto.observacion().trim());
        registro.setEstado(dto.estado().trim());
        registro.setReto(id(dto.reto()) == null ? null : repositorioReto.findById(id(dto.reto()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Reto no encontrado: " + id(dto.reto()))));
        registro.setUsuario(id(dto.usuario()) == null ? null : repositorioUsuario.findById(id(dto.usuario()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id(dto.usuario()))));
    }

    private static UUID id(RefIdDto ref) {
        return ref == null ? null : ref.id();
    }
}
