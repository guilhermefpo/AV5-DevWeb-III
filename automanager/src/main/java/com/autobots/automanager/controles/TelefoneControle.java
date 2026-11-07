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

import com.autobots.automanager.dtos.TelefoneDTO;
import com.autobots.automanager.dtos.TelefoneRespostaDTO;
import com.autobots.automanager.servicos.TelefoneServicos;

@RestController
@RequestMapping("/telefone")
@Validated
public class TelefoneControle {

    @Autowired
    private TelefoneServicos telefoneServicos;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TelefoneRespostaDTO obterTelefone(@PathVariable("id") long id) {
        return links(telefoneServicos.buscarPorId(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TelefoneRespostaDTO> obterTelefones() {
        return telefoneServicos.buscarTelefones().stream().map(this::links).toList();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public TelefoneRespostaDTO cadastrarTelefone(@Valid @RequestBody TelefoneDTO telefoneDto,
            @PathVariable("id") long id) {
        return links(telefoneServicos.cadastrarTelefone(telefoneDto, id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TelefoneRespostaDTO atualizarTelefone(
            @PathVariable("id") long id,
            @RequestBody TelefoneDTO atualizacao) {

        return links(telefoneServicos.atualizarTelefone(id, atualizacao));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirTelefone(@PathVariable("id") long id) {
        telefoneServicos.excluirTelefone(id);
    }

    private TelefoneRespostaDTO links(TelefoneRespostaDTO telefone) {
        telefone.add(linkTo(methodOn(TelefoneControle.class).obterTelefone(telefone.getId())).withSelfRel());
        telefone.add(linkTo(methodOn(TelefoneControle.class).obterTelefones()).withRel("telefones"));
        return telefone;
    }
}
