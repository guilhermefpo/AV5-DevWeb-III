package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.repositorios.CredencialRepositorio;
import javax.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import com.autobots.automanager.dtos.CredencialDTO;
import com.autobots.automanager.dtos.CredencialRespostaDTO;

@Service
public class CredencialServicos {
    @Autowired private CredencialRepositorio credencialRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<CredencialRespostaDTO> listar() {
        return credencialRepositorio.findAll().stream().map(e -> modelMapper.map(e, CredencialRespostaDTO.class)).collect(Collectors.toList());
    }

    public CredencialRespostaDTO buscar(Long id) {
        return modelMapper.map(credencialRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Credencial não encontrada: " + id)), CredencialRespostaDTO.class);
    }

    public CredencialRespostaDTO salvar(Long id, CredencialDTO dto) {
        Credencial e = id == null ? new Credencial() : credencialRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Credencial não encontrada: " + id));
        modelMapper.map(dto, e);
        if (e.getLogin() != null && (e.getId() == null ? credencialRepositorio.existsByLogin(e.getLogin())
                : credencialRepositorio.existsByLoginAndIdNot(e.getLogin(), e.getId()))) {
            throw new DataIntegrityViolationException("Login já cadastrado.");
        }
        return modelMapper.map(credencialRepositorio.save(e), CredencialRespostaDTO.class);
    }

    public void excluir(Long id) {
        credencialRepositorio.delete(credencialRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Credencial não encontrada: " + id)));
    }
}
