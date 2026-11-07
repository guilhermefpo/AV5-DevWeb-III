package com.autobots.automanager.microservicos.vendas;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.autobots.automanager.dtos.VendaRespostaDTO;
import com.autobots.automanager.microservicos.vendas.servicos.VendasConsultaServico;

@RestController
@RequestMapping("/api/empresas/{empresaId}/vendas")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR')")
public class VendasApi {
    @Autowired private VendasConsultaServico vendasConsultaServico;

    @GetMapping
    public List<VendaRespostaDTO> listar(@PathVariable Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return vendasConsultaServico.listarPorEmpresaEPeriodo(empresaId, inicio, fim);
    }
}
