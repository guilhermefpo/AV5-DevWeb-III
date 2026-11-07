package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.dtos.ClienteDTO;
import com.autobots.automanager.dtos.ClienteRespostaDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.excecoes.ClienteJaCadastradoException;
import com.autobots.automanager.excecoes.ClienteNaoEncontradoException;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.entidades.Empresa;
import javax.persistence.EntityNotFoundException;

@Service
public class ClienteServicos {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private ClienteAtualizador clienteAtualizador;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmpresaRepositorio empresaRepositorio;

    public List<ClienteRespostaDTO> buscarClientes() {
        List<Cliente> clientes = clienteRepositorio.findAll();

        return clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteRespostaDTO.class))
                .collect(Collectors.toList());
    }

    public List<ClienteRespostaDTO> buscarClientesPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return List.of();
        return clienteRepositorio.findByCpf(cpf).stream()
                .map(cliente -> modelMapper.map(cliente, ClienteRespostaDTO.class))
                .collect(Collectors.toList());
    }

    public ClienteRespostaDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        return modelMapper.map(cliente, ClienteRespostaDTO.class);
    }

    public ClienteRespostaDTO atualizarCliente(Long id, ClienteDTO novosDados) {
        @SuppressWarnings("null")
        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        Cliente dadosNovos = modelMapper.map(novosDados, Cliente.class);

        clienteAtualizador.atualizar(cliente, dadosNovos);

        @SuppressWarnings("null")
        Cliente clienteSalvo = clienteRepositorio.save(cliente);

        return modelMapper.map(clienteSalvo, ClienteRespostaDTO.class);
    }

    public ClienteRespostaDTO cadastrarCliente(ClienteDTO novoCliente, Long empresaId) {

        if (empresaId == null) throw new IllegalArgumentException("Informe empresaId para associar o cliente.");

        if (clienteRepositorio.existsByCpf(novoCliente.getCpf())) {
            throw new ClienteJaCadastradoException(
                    "Já existe um cliente cadastrado com esse CPF.");
        }

        Cliente cliente = modelMapper.map(novoCliente, Cliente.class);
        cliente.setEmpresa(empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId)));

        if (cliente.getEndereco() != null) {
            cliente.getEndereco().setId(null);
        }
        if (cliente.getDocumentos() != null) {
            cliente.getDocumentos().forEach(documento -> documento.setId(null));
        }
        if (cliente.getTelefones() != null) {
            cliente.getTelefones().forEach(telefone -> telefone.setId(null));
        }

        @SuppressWarnings("null")
        Cliente clienteSalvo = clienteRepositorio.save(cliente);

        return modelMapper.map(clienteSalvo, ClienteRespostaDTO.class);
    }

    public void excluirCliente(Long id) {
        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        clienteRepositorio.delete(cliente);
    }
}
