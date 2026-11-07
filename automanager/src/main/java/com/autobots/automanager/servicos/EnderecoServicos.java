package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.dtos.EnderecoDTO;
import com.autobots.automanager.dtos.EnderecoRespostaDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.excecoes.ClienteNaoEncontradoException;
import com.autobots.automanager.excecoes.EnderecoNaoEncontradoException;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Service
public class EnderecoServicos {

    @Autowired
    private EnderecoRepositorio enderecoRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private EnderecoAtualizador enderecoAtualizador;

    @Autowired
    private ModelMapper modelMapper;

    public List<EnderecoRespostaDTO> buscarEnderecos() {
        List<Endereco> enderecos = enderecoRepositorio.findAll();

        return enderecos.stream()
                .map(endereco -> modelMapper.map(
                        endereco,
                        EnderecoRespostaDTO.class))
                .collect(Collectors.toList());
    }

    public EnderecoRespostaDTO buscarPorId(Long id) {

        @SuppressWarnings("null")
        Endereco endereco = enderecoRepositorio.findById(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(id));

        return modelMapper.map(
                endereco,
                EnderecoRespostaDTO.class);
    }

    public EnderecoRespostaDTO atualizarEndereco(
            Long id,
            EnderecoDTO novosDados) {

        @SuppressWarnings("null")
        Endereco endereco = enderecoRepositorio.findById(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(id));

        Endereco dadosNovos = modelMapper.map(
                novosDados,
                Endereco.class);

        enderecoAtualizador.atualizar(endereco, dadosNovos);

        @SuppressWarnings("null")
        Endereco enderecoSalvo = enderecoRepositorio.save(endereco);

        return modelMapper.map(
                enderecoSalvo,
                EnderecoRespostaDTO.class);
    }

    public EnderecoRespostaDTO cadastrarEndereco(
            EnderecoDTO novoEndereco,
            Long id) {

        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        Endereco endereco = modelMapper.map(
                novoEndereco,
                Endereco.class);

        cliente.setEndereco(endereco);

        Cliente clienteSalvo = clienteRepositorio.save(cliente);

        Endereco enderecoSalvo = clienteSalvo.getEndereco();

        return modelMapper.map(
                enderecoSalvo,
                EnderecoRespostaDTO.class);
    }

    public void excluirEndereco(Long id) {

        Cliente cliente = clienteRepositorio.findAll().stream()
                .filter(c -> c.getEndereco() != null
                        && c.getEndereco().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EnderecoNaoEncontradoException(id));

        cliente.setEndereco(null);

        clienteRepositorio.save(cliente);
    }
}
