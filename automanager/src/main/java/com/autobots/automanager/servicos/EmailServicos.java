package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import javax.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.repositorios.EmailRepositorio;
import com.autobots.automanager.dtos.EmailDTO;
import com.autobots.automanager.dtos.EmailRespostaDTO;

@Service
public class EmailServicos {
    @Autowired private EmailRepositorio emailRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<EmailRespostaDTO> listar() {
        return emailRepositorio.findAll().stream().map(e -> modelMapper.map(e, EmailRespostaDTO.class)).collect(Collectors.toList());
    }

    public EmailRespostaDTO buscar(Long id) {
        return modelMapper.map(emailRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("E-mail não encontrado: " + id)), EmailRespostaDTO.class);
    }

    public EmailRespostaDTO salvar(Long id, EmailDTO dto) {
        Email email = id == null ? new Email() : emailRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("E-mail não encontrado: " + id));
        modelMapper.map(dto, email);
        if (email.getEndereco() != null && (email.getId() == null
                ? emailRepositorio.existsByEndereco(email.getEndereco())
                : emailRepositorio.existsByEnderecoAndIdNot(email.getEndereco(), email.getId()))) {
            throw new DataIntegrityViolationException("E-mail já cadastrado.");
        }
        return modelMapper.map(emailRepositorio.save(email), EmailRespostaDTO.class);
    }

    public void excluir(Long id) {
        emailRepositorio.delete(emailRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("E-mail não encontrado: " + id)));
    }
}
