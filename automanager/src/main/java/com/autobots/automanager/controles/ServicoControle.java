package com.autobots.automanager.controles;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.dtos.ServicoDTO;
import com.autobots.automanager.dtos.ServicoRespostaDTO;
import com.autobots.automanager.servicos.ServicoServicos;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/servico")
public class ServicoControle {
    @Autowired private ServicoServicos servicoServicos;

    @GetMapping
    public List<ServicoRespostaDTO> listar() {
        return servicoServicos.listar().stream().map(this::links).toList();
    }

    @GetMapping("/{id}")
    public ServicoRespostaDTO buscar(@PathVariable Long id) {
        return links(servicoServicos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoRespostaDTO cadastrar(@Valid @RequestBody ServicoDTO e, @RequestParam Long empresaId) {
        return links(servicoServicos.salvar(null, e, empresaId));
    }

    @PutMapping("/{id}")
    public ServicoRespostaDTO atualizar(@PathVariable Long id, @Valid @RequestBody ServicoDTO e,
            @RequestParam(required = false) Long empresaId) {
        return links(servicoServicos.salvar(id, e, empresaId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        servicoServicos.excluir(id);
    }
    private ServicoRespostaDTO links(ServicoRespostaDTO e) { e.add(linkTo(methodOn(ServicoControle.class).buscar(e.getId())).withSelfRel()); e.add(linkTo(methodOn(ServicoControle.class).listar()).withRel("servicos")); return e; }
}
