package com.autobots.automanager.microservicos.veiculos;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.autobots.automanager.dtos.VeiculoRespostaDTO;
import com.autobots.automanager.microservicos.veiculos.servicos.VeiculosConsultaServico;

@RestController
@RequestMapping("/api/empresas/{empresaId}/veiculos-atendidos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR')")
public class VeiculosApi {
    @Autowired private VeiculosConsultaServico veiculosConsultaServico;

    @GetMapping
    public List<VeiculoRespostaDTO> listar(@PathVariable Long empresaId) {
        return veiculosConsultaServico.listarAtendidosPorEmpresa(empresaId);
    }
}
