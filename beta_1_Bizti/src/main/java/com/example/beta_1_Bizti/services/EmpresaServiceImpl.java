package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beta_1_Bizti.dto.EmpresaRequestDto;
import com.example.beta_1_Bizti.dto.EmpresaResponseDto;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.exception.ReglaNegocioException;
import com.example.beta_1_Bizti.mapper.EmpresaMapper;
import com.example.beta_1_Bizti.models.Empresa;
import com.example.beta_1_Bizti.repository.IRepositorioEmpresa;
import com.example.beta_1_Bizti.repository.IRepositorioReto;

@Service
@Transactional
public class EmpresaServiceImpl implements EmpresaService {

    private final IRepositorioEmpresa repositorio;
    private final IRepositorioReto repositorioReto;
    private final EmpresaMapper mapper;

    public EmpresaServiceImpl(IRepositorioEmpresa repositorio, IRepositorioReto repositorioReto, EmpresaMapper mapper) {
        this.repositorio = repositorio;
        this.repositorioReto = repositorioReto;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaResponseDto> listar(Pageable pageable) {
        return repositorio.findAll(pageable).map(mapper::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponseDto buscarPorId(UUID id) {
        return mapper.aDto(obtener(id));
    }

    @Override
    public EmpresaResponseDto crear(EmpresaRequestDto dto) {
        validarUnicos(dto, null);
        Empresa empresa = new Empresa();
        mapper.copiar(dto, empresa);
        return mapper.aDto(repositorio.save(empresa));
    }

    @Override
    public EmpresaResponseDto modificar(UUID id, EmpresaRequestDto dto) {
        Empresa empresa = obtener(id);
        validarUnicos(dto, id);
        mapper.copiar(dto, empresa);
        return mapper.aDto(repositorio.save(empresa));
    }

    @Override
    public void eliminar(UUID id) {
        Empresa empresa = obtener(id);
        if (repositorioReto.existsByEmpresa_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar la empresa: tiene retos asociados");
        }
        repositorio.delete(empresa);
    }

    private Empresa obtener(UUID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada: " + id));
    }

    // id == null al crear; al modificar se excluye la propia empresa
    private void validarUnicos(EmpresaRequestDto dto, UUID id) {
        String nit = dto.nit().trim();
        String correo = dto.correo().trim().toLowerCase();
        if (id == null ? repositorio.existsByNit(nit) : repositorio.existsByNitAndIdNot(nit, id)) {
            throw new ReglaNegocioException("Ya existe una empresa con el NIT " + nit);
        }
        if (id == null ? repositorio.existsByCorreo(correo) : repositorio.existsByCorreoAndIdNot(correo, id)) {
            throw new ReglaNegocioException("Ya existe una empresa con el correo " + correo);
        }
        if (id == null ? repositorio.existsByTelefono(dto.telefono()) : repositorio.existsByTelefonoAndIdNot(dto.telefono(), id)) {
            throw new ReglaNegocioException("Ya existe una empresa con el telefono " + dto.telefono());
        }
    }
}
