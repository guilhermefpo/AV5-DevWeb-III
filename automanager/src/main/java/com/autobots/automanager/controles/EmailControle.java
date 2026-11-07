package com.autobots.automanager.controles;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.dtos.EmailDTO;
import com.autobots.automanager.dtos.EmailRespostaDTO;
import com.autobots.automanager.servicos.EmailServicos;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/email")
public class EmailControle {
    @Autowired
    private EmailServicos emailServicos;

    @GetMapping
    public List<EmailRespostaDTO> listar() {
        return emailServicos.listar().stream().map(this::links).toList();
    }

    @GetMapping("/{id}")
    public EmailRespostaDTO buscar(@PathVariable Long id) {
        return links(emailServicos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmailRespostaDTO cadastrar(@Valid @RequestBody EmailDTO email) {
        return links(emailServicos.salvar(null, email));
    }

    @PutMapping("/{id}")
    public EmailRespostaDTO atualizar(@PathVariable Long id, @Valid @RequestBody EmailDTO email) {
        return links(emailServicos.salvar(id, email));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        emailServicos.excluir(id);
    }

    private EmailRespostaDTO links(EmailRespostaDTO email) {
        email.add(linkTo(methodOn(EmailControle.class).buscar(email.getId())).withSelfRel());
        email.add(linkTo(methodOn(EmailControle.class).listar()).withRel("emails"));
        return email;
    }
}
