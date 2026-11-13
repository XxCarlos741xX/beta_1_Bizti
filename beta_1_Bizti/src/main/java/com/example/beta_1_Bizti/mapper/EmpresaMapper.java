package com.example.beta_1_Bizti.mapper;

import org.springframework.stereotype.Component;

import com.example.beta_1_Bizti.dto.EmpresaRequestDto;
import com.example.beta_1_Bizti.dto.EmpresaResponseDto;
import com.example.beta_1_Bizti.models.Empresa;

@Component
public class EmpresaMapper {

    public void copiar(EmpresaRequestDto dto, Empresa empresa) {
        empresa.setNombre(dto.nombre().trim());
        empresa.setNit(dto.nit().trim());
        empresa.setSector(dto.sector().trim());
        empresa.setContacto(dto.contacto().trim());
        empresa.setCorreo(dto.correo().trim().toLowerCase());
        empresa.setTelefono(dto.telefono());
        empresa.setActiva(dto.activa());
    }

    public EmpresaResponseDto aDto(Empresa e) {
        return new EmpresaResponseDto(e.getId(), e.getNombre(), e.getNit(), e.getSector(),
                e.getContacto(), e.getCorreo(), e.getTelefono(), e.getActiva());
    }
}
