package com.autobots.automanager.modelo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.excecoes.ClienteNaoEncontradoException;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Component
public class ClienteExcluidor {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @SuppressWarnings("null")
    public void excluir(long id) {

        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        clienteRepositorio.delete(cliente);
    }

}