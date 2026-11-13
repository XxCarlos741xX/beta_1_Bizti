package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beta_1_Bizti.dto.CategoriaRequestDto;
import com.example.beta_1_Bizti.dto.CategoriaResponseDto;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.exception.ReglaNegocioException;
import com.example.beta_1_Bizti.mapper.CategoriaMapper;
import com.example.beta_1_Bizti.models.Categoria;
import com.example.beta_1_Bizti.repository.IRepositorioCategoria;
import com.example.beta_1_Bizti.repository.IRepositorioReto;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final IRepositorioCategoria repositorio;
    private final IRepositorioReto repositorioReto;
    private final CategoriaMapper mapper;

    public CategoriaServiceImpl(IRepositorioCategoria repositorio, IRepositorioReto repositorioReto, CategoriaMapper mapper) {
        this.repositorio = repositorio;
        this.repositorioReto = repositorioReto;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResponseDto> listar(Pageable pageable) {
        return repositorio.findAll(pageable).map(mapper::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDto buscarPorId(UUID id) {
        return mapper.aDto(obtener(id));
    }

    @Override
    public CategoriaResponseDto crear(CategoriaRequestDto dto) {
        Categoria categoria = new Categoria();
        mapper.copiar(dto, categoria);
        return mapper.aDto(repositorio.save(categoria));
    }

    @Override
    public CategoriaResponseDto modificar(UUID id, CategoriaRequestDto dto) {
        Categoria categoria = obtener(id);
        mapper.copiar(dto, categoria);
        return mapper.aDto(repositorio.save(categoria));
    }

    @Override
    public void eliminar(UUID id) {
        Categoria categoria = obtener(id);
        if (repositorioReto.existsByCategoria_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar la categoria: tiene retos asociados");
        }
        repositorio.delete(categoria);
    }

    private Categoria obtener(UUID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada: " + id));
    }
}
