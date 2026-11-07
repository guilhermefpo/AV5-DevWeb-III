package com.autobots.automanager.microservicos.clientes;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.autobots.automanager.dtos.ClienteRespostaDTO;
import com.autobots.automanager.microservicos.clientes.servicos.ClientesConsultaServico;

@RestController
@RequestMapping("/api/empresas/{empresaId}/clientes")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR')")
public class ClientesApi { //Lose My Mind - Wiz Khalifa
    @Autowired
    private ClientesConsultaServico clientesConsultaServico;

    @GetMapping
    public List<ClienteRespostaDTO> listar(@PathVariable Long empresaId) {
        return clientesConsultaServico.listarPorEmpresa(empresaId);
    }
}
