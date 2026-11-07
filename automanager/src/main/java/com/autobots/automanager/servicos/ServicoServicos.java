package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import javax.persistence.EntityNotFoundException;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.dtos.ServicoDTO;
import com.autobots.automanager.dtos.ServicoRespostaDTO;

@Service
public class ServicoServicos {
    @Autowired private ServicoRepositorio servicoRepositorio;
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<ServicoRespostaDTO> listar() {
        return servicoRepositorio.findAll().stream().map(e -> modelMapper.map(e, ServicoRespostaDTO.class)).collect(Collectors.toList());
    }

    public ServicoRespostaDTO buscar(Long id) {
        return modelMapper.map(servicoRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado: " + id)), ServicoRespostaDTO.class);
    }

    public ServicoRespostaDTO salvar(Long id, ServicoDTO dto, Long empresaId) {
        Servico e = id == null ? new Servico() : servicoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado: " + id));
        modelMapper.map(dto, e);
        if (empresaId != null) {
            Empresa empresa = empresaRepositorio.findById(empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
            e.setEmpresa(empresa);
        } else if (id == null) {
            throw new IllegalArgumentException("Informe empresaId para associar o serviço.");
        }
        return modelMapper.map(servicoRepositorio.save(e), ServicoRespostaDTO.class);
    }

    public void excluir(Long id) {
        servicoRepositorio.delete(servicoRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado: " + id)));
    }
}
