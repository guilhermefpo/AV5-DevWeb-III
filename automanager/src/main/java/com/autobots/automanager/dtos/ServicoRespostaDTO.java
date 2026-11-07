package com.autobots.automanager.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ServicoRespostaDTO extends RepresentationModel<ServicoRespostaDTO> {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Long empresaId;
    private LocalDateTime dataCadastro;
}
