package com.autobots.automanager.dtos;

import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ClienteRespostaDTO extends RepresentationModel<ClienteRespostaDTO> {

    private Long id;
    private String nome;
    private String cpf;
    private String nomeSocial;
    private Date dataNascimento;
    private Date dataCadastro;
    private Long empresaId;

    private EnderecoRespostaDTO endereco;
    private List<DocumentoRespostaDTO> documentos;
    private List<TelefoneRespostaDTO> telefones;
}
