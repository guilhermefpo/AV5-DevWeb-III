package com.autobots.automanager.dtos;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import com.autobots.automanager.enumeracao.PerfilUsuario;
import java.util.List;

@Data
public class UsuarioDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    private String documento;
    @Valid
    private EmailDTO email;
    @Valid
    private List<DocumentoDTO> documentos;
    @Valid
    private EnderecoDTO endereco;
    @Valid
    private List<TelefoneDTO> telefones;
    @NotNull(message = "Perfil é obrigatório")
    private PerfilUsuario perfil;
    @Valid
    private CredencialDTO credencial;
}
