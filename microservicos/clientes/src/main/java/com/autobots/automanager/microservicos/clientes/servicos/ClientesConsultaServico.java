package com.autobots.automanager.microservicos.clientes.servicos;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.autobots.automanager.dtos.ClienteRespostaDTO;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@Service
public class ClientesConsultaServico {
    @Autowired private ClienteRepositorio clienteRepositorio;
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<ClienteRespostaDTO> listarPorEmpresa(Long empresaId) {
        empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
        return clienteRepositorio.findByEmpresa_Id(empresaId).stream()
                .map(cliente -> modelMapper.map(cliente, ClienteRespostaDTO.class)).collect(Collectors.toList());
    }
}
