package com.autobots.automanager.controles;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.autobots.automanager.dtos.VendaDTO;
import com.autobots.automanager.dtos.VendaRespostaDTO;
import com.autobots.automanager.servicos.VendaServicos;
import com.autobots.automanager.seguranca.AcessoUsuario;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/venda")
public class VendaControle {
    private final VendaServicos servicos;
    private final AcessoUsuario acessoUsuario;

    public VendaControle(VendaServicos servicos, AcessoUsuario acessoUsuario) {
        this.servicos = servicos;
        this.acessoUsuario = acessoUsuario;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR','CLIENTE')")
    public List<VendaRespostaDTO> listar() {
        List<VendaRespostaDTO> vendas = acessoUsuario.podeVerTodasVendas()
                ? servicos.listar()
                : (acessoUsuario.eVendedor()
                        ? servicos.listarCriadasPor(acessoUsuario.loginAtualPublico())
                        : servicos.listarDoUsuario(acessoUsuario.loginAtualPublico()));
        return vendas.stream().map(this::links).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acessoUsuario.podeAcessarVenda(#id)")
    public VendaRespostaDTO buscar(@PathVariable @P("id") Long id) {
        return links(servicos.buscar(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public VendaRespostaDTO cadastrar(@Valid @RequestBody VendaDTO dto) {
        return links(servicos.salvar(null, dto, acessoUsuario.idUsuarioAtual()));
    }

    @PutMapping("/{id}")
    public VendaRespostaDTO atualizar(@PathVariable Long id, @Valid @RequestBody VendaDTO dto) {
        return links(servicos.salvar(id, dto, acessoUsuario.idUsuarioAtual()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        servicos.excluir(id);
    }

    private VendaRespostaDTO links(VendaRespostaDTO dto) {
        dto.add(linkTo(methodOn(VendaControle.class).buscar(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(VendaControle.class).listar()).withRel("vendas"));
        return dto;
    }
}
