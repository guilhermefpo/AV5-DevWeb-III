package com.autobots.automanager.controles;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import com.autobots.automanager.dtos.UsuarioDTO;
import com.autobots.automanager.dtos.UsuarioRespostaDTO;
import com.autobots.automanager.servicos.UsuarioServicos;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioControle {
    private final UsuarioServicos s;

    public UsuarioControle(UsuarioServicos s) {
        this.s = s;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR','CLIENTE')")
    public List<UsuarioRespostaDTO> listar() {
        return s.listar().stream().map(this::links).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acessoUsuario.podeLerUsuario(#id)")
    public UsuarioRespostaDTO buscar(@PathVariable @P("id") Long id) {
        return links(s.buscar(id));
    }

    @PostMapping
    @PreAuthorize("@acessoUsuario.podeCriarUsuario(#dto.perfil)")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRespostaDTO cadastrar(@Valid @RequestBody @P("dto") UsuarioDTO e,
            @RequestParam(required = false) Long empresaId) {
        return links(s.salvar(null, e, empresaId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acessoUsuario.podeAlterarUsuario(#id, #dto.perfil)")
    public UsuarioRespostaDTO atualizar(@PathVariable @P("id") Long id, @Valid @RequestBody @P("dto") UsuarioDTO e,
            @RequestParam(required = false) Long empresaId) {
        return links(s.salvar(id, e, empresaId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acessoUsuario.podeExcluirUsuario(#id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable @P("id") Long id) {
        s.excluir(id);
    }

    private UsuarioRespostaDTO links(UsuarioRespostaDTO e) {
        e.add(linkTo(methodOn(UsuarioControle.class).buscar(e.getId())).withSelfRel());
        e.add(linkTo(methodOn(UsuarioControle.class).listar()).withRel("usuarios"));
        return e;
    }
}
