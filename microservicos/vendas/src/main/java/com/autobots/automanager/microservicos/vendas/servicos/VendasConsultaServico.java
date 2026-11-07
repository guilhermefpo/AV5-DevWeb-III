package com.autobots.automanager.microservicos.vendas.servicos;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.autobots.automanager.dtos.ItemVendaRespostaDTO;
import com.autobots.automanager.dtos.VendaRespostaDTO;
import com.autobots.automanager.entidades.OrdemServico;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracao.StatusOrdemServico;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.OrdemServicoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
public class VendasConsultaServico {
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private VendaRepositorio vendaRepositorio;
    @Autowired private OrdemServicoRepositorio ordemServicoRepositorio;

    public List<VendaRespostaDTO> listarPorEmpresaEPeriodo(Long empresaId, LocalDate inicio, LocalDate fim) {
        empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data fim deve ser igual ou posterior à data início.");
        }
        List<VendaRespostaDTO> movimentacoes = vendaRepositorio
                .findByEmpresa_IdAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                        empresaId, inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay())
                .stream().map(this::paraVenda).collect(Collectors.toList());
        ordemServicoRepositorio.findByEmpresa_IdAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                empresaId, inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay()).stream()
                .filter(ordem -> ordem.getStatus() == StatusOrdemServico.CONCLUIDA)
                .map(this::paraOrdemServico).forEach(movimentacoes::add);
        movimentacoes.sort((a, b) -> a.getDataCriacao().compareTo(b.getDataCriacao()));
        return movimentacoes;
    }

    private VendaRespostaDTO paraVenda(Venda venda) {
        VendaRespostaDTO dto = new VendaRespostaDTO();
        dto.setId(venda.getId());
        dto.setTipo("PECAS");
        dto.setEmpresaId(venda.getEmpresa().getId());
        dto.setUsuarioId(venda.getUsuario().getId());
        dto.setVeiculoId(venda.getVeiculo() == null ? null : venda.getVeiculo().getId());
        dto.setDataCriacao(venda.getDataCriacao());
        dto.setTotal(venda.getTotal());
        dto.setItens(venda.getItens().stream().map(item -> {
            ItemVendaRespostaDTO resposta = new ItemVendaRespostaDTO();
            resposta.setMercadoriaId(item.getMercadoria().getId());
            resposta.setMercadoriaNome(item.getMercadoria().getNome());
            resposta.setQuantidade(item.getQuantidade());
            resposta.setPrecoUnitario(item.getPrecoUnitario());
            resposta.setSubtotal(item.getSubtotal());
            return resposta;
        }).collect(Collectors.toList()));
        return dto;
    }

    private VendaRespostaDTO paraOrdemServico(OrdemServico ordem) {
        VendaRespostaDTO dto = new VendaRespostaDTO();
        dto.setId(ordem.getId());
        dto.setTipo("SERVICO");
        dto.setEmpresaId(ordem.getEmpresa().getId());
        dto.setUsuarioId(ordem.getUsuario().getId());
        dto.setVeiculoId(ordem.getVeiculo().getId());
        dto.setDataCriacao(ordem.getDataCriacao());
        dto.setTotal(ordem.getValorCobrado());
        dto.setServicoId(ordem.getServico().getId());
        dto.setServicoNome(ordem.getServico().getNome());
        dto.setObservacao(ordem.getObservacao());
        dto.setStatus(ordem.getStatus());
        return dto;
    }
}
