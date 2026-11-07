package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.autobots.automanager.dtos.EnderecoDTO;
import com.autobots.automanager.dtos.EnderecoRespostaDTO;
import com.autobots.automanager.servicos.EnderecoServicos;

@RestController
@RequestMapping("/endereco")
@Validated
public class EnderecoControle {

    @Autowired
    private EnderecoServicos enderecoServicos;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EnderecoRespostaDTO obterEndereco(@PathVariable("id") long id) {
        return links(enderecoServicos.buscarPorId(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EnderecoRespostaDTO> obterEnderecos() {
        return enderecoServicos.buscarEnderecos().stream().map(this::links).toList();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public EnderecoRespostaDTO cadastrarEndereco(@Valid @RequestBody EnderecoDTO enderecoDto,
            @PathVariable("id") long id) {
        return links(enderecoServicos.cadastrarEndereco(enderecoDto, id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EnderecoRespostaDTO atualizarEndereco(@PathVariable("id") long id, @RequestBody EnderecoDTO atualizacao) {
        return links(enderecoServicos.atualizarEndereco(id, atualizacao));
    }

    @SuppressWarnings("null")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirEndereco(@PathVariable("id") long id) {
        enderecoServicos.excluirEndereco(id);
    }

    private EnderecoRespostaDTO links(EnderecoRespostaDTO endereco) {
        endereco.add(linkTo(methodOn(EnderecoControle.class).obterEndereco(endereco.getId())).withSelfRel());
        endereco.add(linkTo(methodOn(EnderecoControle.class).obterEnderecos()).withRel("enderecos"));
        return endereco;
    }
}
