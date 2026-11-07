package com.autobots.automanager.microservicos.catalogo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.autobots.automanager.dtos.CatalogoRespostaDTO;
import com.autobots.automanager.microservicos.catalogo.servicos.CatalogoConsultaServico;

@RestController
@RequestMapping("/api/empresas/{empresaId}/catalogo")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR')")
public class CatalogoApi {
    @Autowired private CatalogoConsultaServico catalogoConsultaServico;

    @GetMapping
    public CatalogoRespostaDTO listar(@PathVariable Long empresaId) {
        return catalogoConsultaServico.listarPorEmpresa(empresaId);
    }
}
