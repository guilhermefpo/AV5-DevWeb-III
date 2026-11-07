package com.autobots.automanager.controles;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.dtos.MercadoriaDTO;
import com.autobots.automanager.dtos.MercadoriaRespostaDTO;
import com.autobots.automanager.servicos.MercadoriaServicos;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {
    @Autowired private MercadoriaServicos mercadoriaServicos;

    @GetMapping
    public List<MercadoriaRespostaDTO> listar() {
        return mercadoriaServicos.listar().stream().map(this::links).toList();
    }

    @GetMapping("/{id}")
    public MercadoriaRespostaDTO buscar(@PathVariable Long id) {
        return links(mercadoriaServicos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MercadoriaRespostaDTO cadastrar(@Valid @RequestBody MercadoriaDTO e, @RequestParam Long empresaId) {
        return links(mercadoriaServicos.salvar(null, e, empresaId));
    }

    @PutMapping("/{id}")
    public MercadoriaRespostaDTO atualizar(@PathVariable Long id, @Valid @RequestBody MercadoriaDTO e,
            @RequestParam(required = false) Long empresaId) {
        return links(mercadoriaServicos.salvar(id, e, empresaId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        mercadoriaServicos.excluir(id);
    }
    private MercadoriaRespostaDTO links(MercadoriaRespostaDTO e) { e.add(linkTo(methodOn(MercadoriaControle.class).buscar(e.getId())).withSelfRel()); e.add(linkTo(methodOn(MercadoriaControle.class).listar()).withRel("mercadorias")); return e; }
}
