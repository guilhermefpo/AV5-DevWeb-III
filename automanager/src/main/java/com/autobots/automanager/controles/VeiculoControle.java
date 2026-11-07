package com.autobots.automanager.controles;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.autobots.automanager.dtos.VeiculoDTO;
import com.autobots.automanager.dtos.VeiculoRespostaDTO;
import com.autobots.automanager.servicos.VeiculoServicos;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/veiculo")
public class VeiculoControle {
    @Autowired private VeiculoServicos veiculoServicos;

    @GetMapping
    public List<VeiculoRespostaDTO> listar() {
        return veiculoServicos.listar().stream().map(this::links).toList();
    }

    @GetMapping("/{id}")
    public VeiculoRespostaDTO buscar(@PathVariable Long id) {
        return links(veiculoServicos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeiculoRespostaDTO cadastrar(@Valid @RequestBody VeiculoDTO e, @RequestParam Long usuarioId) {
        return links(veiculoServicos.salvar(null, e, usuarioId));
    }

    @PutMapping("/{id}")
    public VeiculoRespostaDTO atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoDTO e,
            @RequestParam(required = false) Long usuarioId) {
        return links(veiculoServicos.salvar(id, e, usuarioId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        veiculoServicos.excluir(id);
    }

    private VeiculoRespostaDTO links(VeiculoRespostaDTO e) {
        e.add(linkTo(methodOn(VeiculoControle.class).buscar(e.getId())).withSelfRel());
        e.add(linkTo(methodOn(VeiculoControle.class).listar()).withRel("veiculos"));
        return e;
    }
}
