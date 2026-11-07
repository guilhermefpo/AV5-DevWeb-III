package com.autobots.automanager.controles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.autobots.automanager.dtos.ClienteRespostaDTO;
import com.autobots.automanager.dtos.MercadoriaRespostaDTO;
import com.autobots.automanager.dtos.ServicoRespostaDTO;
import com.autobots.automanager.dtos.UsuarioRespostaDTO;
import com.autobots.automanager.dtos.VeiculoRespostaDTO;
import com.autobots.automanager.dtos.VendaRespostaDTO;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.entidades.OrdemServico;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.OrdemServicoRepositorio;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
@RequestMapping("/api/empresas/{empresaId}")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR')")
public class ConsultaEmpresaControle {
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private ClienteRepositorio clienteRepositorio;
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private MercadoriaRepositorio mercadoriaRepositorio;
    @Autowired private ServicoRepositorio servicoRepositorio;
    @Autowired private VendaRepositorio vendaRepositorio;
    @Autowired private OrdemServicoRepositorio ordemServicoRepositorio;
    @Autowired private ModelMapper modelMapper;

    @GetMapping("/clientes")
    public List<ClienteRespostaDTO> clientes(@PathVariable Long empresaId) {
        validarEmpresa(empresaId);
        return clienteRepositorio.findByEmpresa_Id(empresaId).stream()
                .map(e -> modelMapper.map(e, ClienteRespostaDTO.class)).collect(Collectors.toList());
    }

    @GetMapping("/funcionarios")
    public List<UsuarioRespostaDTO> funcionarios(@PathVariable Long empresaId) {
        validarEmpresa(empresaId);
        return usuarioRepositorio.findByEmpresa_Id(empresaId).stream()
                .filter(e -> e.getPerfil() != com.autobots.automanager.enumeracao.PerfilUsuario.CLIENTE)
                .map(e -> { UsuarioRespostaDTO dto = modelMapper.map(e, UsuarioRespostaDTO.class); dto.setCredencial(null); return dto; })
                .collect(Collectors.toList());
    }

    @GetMapping({"/catalogo", "/mercadorias-servicos"})
    public Catalogo catalogo(@PathVariable Long empresaId) {
        validarEmpresa(empresaId);
        return new Catalogo(
                mercadoriaRepositorio.findByEmpresa_IdAndEstoqueGreaterThan(empresaId, 0).stream()
                        .map(e -> modelMapper.map(e, MercadoriaRespostaDTO.class)).collect(Collectors.toList()),
                servicoRepositorio.findByEmpresa_Id(empresaId).stream()
                        .map(e -> modelMapper.map(e, ServicoRespostaDTO.class)).collect(Collectors.toList()));
    }

    @GetMapping("/vendas")
    public List<VendaRespostaDTO> vendas(@PathVariable Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        validarEmpresa(empresaId);
        if (fim.isBefore(inicio)) throw new IllegalArgumentException("A data fim deve ser igual ou posterior à data início.");
        List<VendaRespostaDTO> movimentacoes = vendaRepositorio.findByEmpresa_IdAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                empresaId, inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay()).stream()
                .map(this::paraVenda).collect(Collectors.toList());
        ordemServicoRepositorio.findByEmpresa_IdAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                empresaId, inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay()).stream()
                .filter(ordem -> ordem.getStatus() == com.autobots.automanager.enumeracao.StatusOrdemServico.CONCLUIDA)
                .map(this::paraOrdemServico).forEach(movimentacoes::add);
        movimentacoes.sort((a, b) -> a.getDataCriacao().compareTo(b.getDataCriacao()));
        return movimentacoes;
    }

    @GetMapping("/veiculos-atendidos")
    public List<VeiculoRespostaDTO> veiculosAtendidos(@PathVariable Long empresaId) {
        validarEmpresa(empresaId);
        return ordemServicoRepositorio.findByEmpresa_Id(empresaId).stream()
                .filter(ordem -> ordem.getStatus() == com.autobots.automanager.enumeracao.StatusOrdemServico.CONCLUIDA)
                .map(e -> e.getVeiculo())
                .collect(Collectors.toMap(e -> e.getId(), e -> e, (a, b) -> a)).values().stream()
                .map(e -> modelMapper.map(e, VeiculoRespostaDTO.class)).collect(Collectors.toList());
    }

    private void validarEmpresa(Long id) {
        empresaRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + id));
    }

    private VendaRespostaDTO paraVenda(Venda venda) {
        VendaRespostaDTO dto = new VendaRespostaDTO();
        dto.setTipo("PECAS");
        dto.setId(venda.getId());
        dto.setEmpresaId(venda.getEmpresa().getId());
        dto.setUsuarioId(venda.getUsuario().getId());
        dto.setVeiculoId(venda.getVeiculo() == null ? null : venda.getVeiculo().getId());
        dto.setDataCriacao(venda.getDataCriacao());
        dto.setTotal(venda.getTotal());
        dto.setItens(venda.getItens().stream().map(e -> {
            com.autobots.automanager.dtos.ItemVendaRespostaDTO item = new com.autobots.automanager.dtos.ItemVendaRespostaDTO();
            item.setMercadoriaId(e.getMercadoria().getId());
            item.setMercadoriaNome(e.getMercadoria().getNome());
            item.setQuantidade(e.getQuantidade());
            item.setPrecoUnitario(e.getPrecoUnitario());
            item.setSubtotal(e.getSubtotal());
            return item;
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

    public static class Catalogo {
        private final List<MercadoriaRespostaDTO> mercadorias;
        private final List<ServicoRespostaDTO> servicos;
        public Catalogo(List<MercadoriaRespostaDTO> mercadorias, List<ServicoRespostaDTO> servicos) {
            this.mercadorias = mercadorias;
            this.servicos = servicos;
        }
        public List<MercadoriaRespostaDTO> getMercadorias() { return mercadorias; }
        public List<ServicoRespostaDTO> getServicos() { return servicos; }
    }
}
