package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.autobots.automanager.dtos.OrdemServicoDTO;
import com.autobots.automanager.dtos.OrdemServicoRespostaDTO;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.OrdemServico;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.enumeracao.StatusOrdemServico;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.OrdemServicoRepositorio;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;

@Service
@Transactional
public class OrdemServicoServicos {
    @Autowired private OrdemServicoRepositorio ordemServicoRepositorio;
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private VeiculoRepositorio veiculoRepositorio;
    @Autowired private ServicoRepositorio servicoRepositorio;

    @Transactional(readOnly = true)
    public List<OrdemServicoRespostaDTO> listar() {
        return ordemServicoRepositorio.findAll().stream().map(this::paraResposta).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdemServicoRespostaDTO buscar(Long id) {
        return paraResposta(obterOrdem(id));
    }

    public OrdemServicoRespostaDTO salvar(Long id, OrdemServicoDTO dto) {
        OrdemServico ordem = id == null ? new OrdemServico() : obterOrdem(id);
        Empresa empresa = empresaRepositorio.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + dto.getEmpresaId()));
        Usuario usuario = usuarioRepositorio.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + dto.getUsuarioId()));
        Veiculo veiculo = veiculoRepositorio.findById(dto.getVeiculoId())
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + dto.getVeiculoId()));
        Servico servico = servicoRepositorio.findById(dto.getServicoId())
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado: " + dto.getServicoId()));

        if (veiculo.getUsuario() == null || !veiculo.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("O veículo informado não pertence ao usuário informado.");
        }
        if (servico.getEmpresa() == null || !servico.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("O serviço informado não pertence à empresa informada.");
        }

        ordem.setEmpresa(empresa);
        ordem.setUsuario(usuario);
        ordem.setVeiculo(veiculo);
        ordem.setServico(servico);
        ordem.setValorCobrado(servico.getPreco());
        ordem.setObservacao(dto.getObservacao());
        if (dto.getStatus() != null) ordem.setStatus(dto.getStatus());
        else if (id == null) ordem.setStatus(StatusOrdemServico.ABERTA);
        return paraResposta(ordemServicoRepositorio.save(ordem));
    }

    public void excluir(Long id) {
        ordemServicoRepositorio.delete(obterOrdem(id));
    }

    private OrdemServico obterOrdem(Long id) {
        return ordemServicoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada: " + id));
    }

    private OrdemServicoRespostaDTO paraResposta(OrdemServico ordem) {
        OrdemServicoRespostaDTO dto = new OrdemServicoRespostaDTO();
        dto.setId(ordem.getId());
        dto.setEmpresaId(ordem.getEmpresa().getId());
        dto.setUsuarioId(ordem.getUsuario().getId());
        dto.setVeiculoId(ordem.getVeiculo().getId());
        dto.setPlacaVeiculo(ordem.getVeiculo().getPlaca());
        dto.setServicoId(ordem.getServico().getId());
        dto.setNomeServico(ordem.getServico().getNome());
        dto.setValorCobrado(ordem.getValorCobrado());
        dto.setObservacao(ordem.getObservacao());
        dto.setDataCriacao(ordem.getDataCriacao());
        dto.setStatus(ordem.getStatus());
        return dto;
    }
}
