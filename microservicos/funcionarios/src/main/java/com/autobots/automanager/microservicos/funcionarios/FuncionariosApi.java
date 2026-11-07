package com.autobots.automanager.microservicos.funcionarios;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.autobots.automanager.dtos.UsuarioRespostaDTO;
import com.autobots.automanager.microservicos.funcionarios.servicos.FuncionariosConsultaServico;

@RestController
@RequestMapping("/api/empresas/{empresaId}/funcionarios")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR')")
public class FuncionariosApi {
    @Autowired private FuncionariosConsultaServico funcionariosConsultaServico;

    @GetMapping
    public List<UsuarioRespostaDTO> listar(@PathVariable Long empresaId) {
        return funcionariosConsultaServico.listarPorEmpresa(empresaId);
    }
}
