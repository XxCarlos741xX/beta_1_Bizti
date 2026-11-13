package com.example.beta_1_Bizti.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beta_1_Bizti.dto.CuentaRegistroDto;
import com.example.beta_1_Bizti.dto.LoginDto;
import com.example.beta_1_Bizti.dto.UsuarioCrearDto;
import com.example.beta_1_Bizti.dto.UsuarioModificarDto;
import com.example.beta_1_Bizti.dto.UsuarioResponseDto;
import com.example.beta_1_Bizti.exception.CredencialesInvalidasException;
import com.example.beta_1_Bizti.exception.RecursoNoEncontradoException;
import com.example.beta_1_Bizti.exception.ReglaNegocioException;
import com.example.beta_1_Bizti.mapper.UsuarioMapper;
import com.example.beta_1_Bizti.models.Usuario;
import com.example.beta_1_Bizti.repository.IRepositorioRegistro;
import com.example.beta_1_Bizti.repository.IRepositorioUsuario;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final String ROL_POR_DEFECTO = "usuario";

    private final IRepositorioUsuario repositorio;
    private final IRepositorioRegistro repositorioRegistro;
    private final UsuarioMapper mapper;
    private final HashContrasena hash;

    public UsuarioServiceImpl(IRepositorioUsuario repositorio, IRepositorioRegistro repositorioRegistro,
            UsuarioMapper mapper, HashContrasena hash) {
        this.repositorio = repositorio;
        this.repositorioRegistro = repositorioRegistro;
        this.mapper = mapper;
        this.hash = hash;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDto> listar(Pageable pageable) {
        return repositorio.findAll(pageable).map(mapper::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto buscarPorId(UUID id) {
        return mapper.aDto(obtener(id));
    }

    @Override
    public UsuarioResponseDto crear(UsuarioCrearDto dto) {
        String rol = dto.rol() == null || dto.rol().isBlank() ? ROL_POR_DEFECTO : dto.rol();
        return mapper.aDto(nuevo(dto.nombre(), dto.correo(), dto.contrasena(), rol));
    }

    @Override
    public UsuarioResponseDto registrar(CuentaRegistroDto dto) {
        return mapper.aDto(nuevo(dto.nombre(), dto.correo(), dto.contrasena(), ROL_POR_DEFECTO));
    }

    @Override
    public UsuarioResponseDto modificar(UUID id, UsuarioModificarDto dto) {
        Usuario usuario = obtener(id);
        String correo = normalizar(dto.correo());
        if (repositorio.existsByCorreoAndIdNot(correo, id)) {
            throw new ReglaNegocioException("Ya existe un usuario con el correo " + correo);
        }
        usuario.setNombre(dto.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setRol(dto.rol());
        if (dto.contrasena() != null && !dto.contrasena().isBlank()) {
            usuario.setContrasena(hash.hashear(dto.contrasena()));
        }
        return mapper.aDto(repositorio.save(usuario));
    }

    @Override
    public void eliminar(UUID id) {
        Usuario usuario = obtener(id);
        if (repositorioRegistro.existsByUsuario_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar el usuario: tiene registros asociados");
        }
        repositorio.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto login(LoginDto dto) {
        Usuario usuario = repositorio.findByCorreo(normalizar(dto.correo()))
                .filter(u -> hash.coincide(dto.contrasena(), u.getContrasena()))
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contrasena incorrectos"));
        return mapper.aDto(usuario);
    }

    private Usuario nuevo(String nombre, String correo, String contrasena, String rol) {
        String correoNormal = normalizar(correo);
        if (repositorio.existsByCorreo(correoNormal)) {
            throw new ReglaNegocioException("Ya existe un usuario con el correo " + correoNormal);
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setCorreo(correoNormal);
        usuario.setContrasena(hash.hashear(contrasena));
        usuario.setRol(rol);
        return repositorio.save(usuario);
    }

    private Usuario obtener(UUID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    private static String normalizar(String correo) {
        return correo.trim().toLowerCase();
    }
}
