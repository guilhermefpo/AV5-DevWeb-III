package com.autobots.automanager.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class MercadoriaRespostaDTO extends RepresentationModel<MercadoriaRespostaDTO> {
    private Long id;
    private String nome;
    private String descricao;
    private Integer estoque;
    private BigDecimal preco;
    private Long empresaId;
    private LocalDateTime dataCadastro;
}
