package com.autobots.automanager.controles;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.dtos.EmpresaDTO;
import com.autobots.automanager.dtos.EmpresaRespostaDTO;
import com.autobots.automanager.servicos.EmpresaServicos;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/empresa")
public class EmpresaControle {

    @Autowired
    private EmpresaServicos empresaServicos;

    @GetMapping
    public List<EmpresaRespostaDTO> listar() {
        return empresaServicos.listar().stream().map(this::links).toList();
    }

    @GetMapping("/{id}")
    public EmpresaRespostaDTO buscar(@PathVariable Long id) {
        return links(empresaServicos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaRespostaDTO cadastrar(@Valid @RequestBody EmpresaDTO e) {
        return links(empresaServicos.salvar(null, e));
    }

    @PutMapping("/{id}")
    public EmpresaRespostaDTO atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaDTO e) {
        return links(empresaServicos.salvar(id, e));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        empresaServicos.excluir(id);
    }
    private EmpresaRespostaDTO links(EmpresaRespostaDTO e) { e.add(linkTo(methodOn(EmpresaControle.class).buscar(e.getId())).withSelfRel()); e.add(linkTo(methodOn(EmpresaControle.class).listar()).withRel("empresas")); return e; }
}
