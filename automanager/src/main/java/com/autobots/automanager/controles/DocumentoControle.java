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

import com.autobots.automanager.dtos.DocumentoDTO;
import com.autobots.automanager.dtos.DocumentoRespostaDTO;
import com.autobots.automanager.servicos.DocumentoServicos;

@RestController
@RequestMapping("/documento")
@Validated
public class DocumentoControle {
    @Autowired
    private DocumentoServicos documentoServicos;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DocumentoRespostaDTO buscarDocumento(@PathVariable("id") long id) {
        return links(documentoServicos.buscarPorId(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentoRespostaDTO> obterDocumentos() {
        return documentoServicos.buscarDocumentos().stream().map(this::links).toList();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoRespostaDTO cadastrarDocumento(@Valid @RequestBody DocumentoDTO documentoDTO,
            @PathVariable("id") long id) {
        return links(documentoServicos.cadastrarDocumento(documentoDTO, id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DocumentoRespostaDTO atualizarDocumento(@PathVariable("id") long id, @RequestBody DocumentoDTO novosDados) {
        return links(documentoServicos.atualizarDocumento(id, novosDados));
    }

    @SuppressWarnings("null")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirDocumento(@PathVariable("id") long id) {
        documentoServicos.excluirDocumento(id);
    }

    private DocumentoRespostaDTO links(DocumentoRespostaDTO documento) {
        documento.add(linkTo(methodOn(DocumentoControle.class).buscarDocumento(documento.getId())).withSelfRel());
        documento.add(linkTo(methodOn(DocumentoControle.class).obterDocumentos()).withRel("documentos"));
        return documento;
    }
}
