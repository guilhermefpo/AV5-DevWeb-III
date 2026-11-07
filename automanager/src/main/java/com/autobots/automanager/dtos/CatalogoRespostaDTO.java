package com.autobots.automanager.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoRespostaDTO {
    private List<MercadoriaRespostaDTO> mercadorias;
    private List<ServicoRespostaDTO> servicos;
}
