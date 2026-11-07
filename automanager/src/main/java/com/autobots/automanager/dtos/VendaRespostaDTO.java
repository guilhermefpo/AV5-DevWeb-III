package com.autobots.automanager.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import com.autobots.automanager.enumeracao.StatusOrdemServico;

@Data
public class VendaRespostaDTO extends RepresentationModel<VendaRespostaDTO> {
    private Long id;
    private Long empresaId;
    private Long usuarioId;
    private Long veiculoId;
    private LocalDateTime dataCriacao;
    private BigDecimal total;
    private List<ItemVendaRespostaDTO> itens;
    private String tipo;
    private Long servicoId;
    private String servicoNome;
    private String observacao;
    private StatusOrdemServico status;
}
