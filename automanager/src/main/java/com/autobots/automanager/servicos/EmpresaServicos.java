package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.dtos.EmpresaDTO;
import com.autobots.automanager.dtos.EmpresaRespostaDTO;
import javax.persistence.EntityNotFoundException;

@Service
public class EmpresaServicos {
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<EmpresaRespostaDTO> listar() {
        return empresaRepositorio.findAll().stream().map(e -> modelMapper.map(e, EmpresaRespostaDTO.class)).collect(Collectors.toList());
    }

    public EmpresaRespostaDTO buscar(Long id) {
        return modelMapper.map(
                empresaRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + id)),
                EmpresaRespostaDTO.class);
    }

    public EmpresaRespostaDTO salvar(Long id, EmpresaDTO dto) {
        Empresa e = id == null ? new Empresa()
                : empresaRepositorio.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + id));
        modelMapper.map(dto, e);
        return modelMapper.map(empresaRepositorio.save(e), EmpresaRespostaDTO.class);
    }

    public void excluir(Long id) {
        empresaRepositorio.delete(empresaRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + id)));
    }
}
