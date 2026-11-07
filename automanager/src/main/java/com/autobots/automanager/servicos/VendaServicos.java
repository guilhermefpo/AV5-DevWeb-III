package com.autobots.automanager.servicos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.autobots.automanager.dtos.ItemVendaDTO;
import com.autobots.automanager.dtos.ItemVendaRespostaDTO;
import com.autobots.automanager.dtos.VendaDTO;
import com.autobots.automanager.dtos.VendaRespostaDTO;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.ItemVenda;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
@Transactional
public class VendaServicos {
    @Autowired private VendaRepositorio vendaRepositorio;
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private MercadoriaRepositorio mercadoriaRepositorio;
    @Autowired private VeiculoRepositorio veiculoRepositorio;

    @Transactional(readOnly = true)
    public List<VendaRespostaDTO> listar() {
        return vendaRepositorio.findAll().stream().map(this::paraResposta).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaRespostaDTO> listarDoUsuario(String login) {
        return vendaRepositorio.findByUsuario_Credencial_Login(login).stream()
                .map(this::paraResposta).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaRespostaDTO> listarCriadasPor(String login) {
        return vendaRepositorio.findByCriadoPor_Credencial_Login(login).stream()
                .map(this::paraResposta).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VendaRespostaDTO buscar(Long id) {
        return paraResposta(obterVenda(id));
    }

    public VendaRespostaDTO salvar(Long id, VendaDTO dto, Long criadoPorId) {
        Venda venda = id == null ? new Venda() : obterVenda(id);

        if (id != null)
            restaurarEstoque(venda);

        Empresa empresa = empresaRepositorio.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + dto.getEmpresaId()));
        Usuario usuario = usuarioRepositorio.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + dto.getUsuarioId()));
        Veiculo veiculo = null;
        if (dto.getVeiculoId() != null) {
            veiculo = veiculoRepositorio.findById(dto.getVeiculoId())
                    .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + dto.getVeiculoId()));
            if (veiculo.getUsuario() == null || !veiculo.getUsuario().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("O veículo informado não pertence ao usuário informado.");
            }
        }
        venda.setEmpresa(empresa);
        venda.setUsuario(usuario);
        Usuario criador = usuarioRepositorio.findById(criadoPorId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado: " + criadoPorId));
        venda.setCriadoPor(criador);
        venda.setVeiculo(veiculo);
        venda.getItens().clear();

        Map<Long, Integer> quantidades = new LinkedHashMap<>();
        for (ItemVendaDTO item : dto.getItens()) {
            quantidades.merge(item.getMercadoriaId(), item.getQuantidade(), Integer::sum);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entrada : quantidades.entrySet()) {
            Mercadoria mercadoria = mercadoriaRepositorio.findById(entrada.getKey())
                    .orElseThrow(() -> new EntityNotFoundException("Mercadoria não encontrada: " + entrada.getKey()));
            if (mercadoria.getEmpresa() == null || !mercadoria.getEmpresa().getId().equals(empresa.getId())) {
                throw new IllegalArgumentException("A mercadoria " + mercadoria.getId()
                        + " não pertence à empresa informada.");
            }
            if (mercadoria.getEstoque() == null || mercadoria.getEstoque() < entrada.getValue()) {
                throw new IllegalArgumentException("Estoque insuficiente para a mercadoria " + mercadoria.getId());
            }

            BigDecimal subtotal = mercadoria.getPreco().multiply(BigDecimal.valueOf(entrada.getValue()));
            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setMercadoria(mercadoria);
            item.setQuantidade(entrada.getValue());
            item.setPrecoUnitario(mercadoria.getPreco());
            item.setSubtotal(subtotal);
            venda.getItens().add(item);
            mercadoria.setEstoque(mercadoria.getEstoque() - entrada.getValue());
            total = total.add(subtotal);
        }

        venda.setTotal(total);
        return paraResposta(vendaRepositorio.save(venda));
    }

    public void excluir(Long id) {
        Venda venda = obterVenda(id);
        restaurarEstoque(venda);
        vendaRepositorio.delete(venda);
    }

    private Venda obterVenda(Long id) {
        return vendaRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Venda não encontrada: " + id));
    }

    private void restaurarEstoque(Venda venda) {
        for (ItemVenda item : new ArrayList<>(venda.getItens())) {
            Mercadoria mercadoria = item.getMercadoria();
            if (mercadoria.getEstoque() == null) {
                throw new IllegalArgumentException("Não é possível restaurar o estoque da mercadoria "
                        + mercadoria.getId() + " porque o estoque não foi informado.");
            }
            mercadoria.setEstoque(mercadoria.getEstoque() + item.getQuantidade());
        }
    }

    private VendaRespostaDTO paraResposta(Venda venda) {
        VendaRespostaDTO dto = new VendaRespostaDTO();
        dto.setTipo("PECAS");
        dto.setId(venda.getId());
        dto.setEmpresaId(venda.getEmpresa().getId());
        dto.setUsuarioId(venda.getUsuario().getId());
        dto.setVeiculoId(venda.getVeiculo() == null ? null : venda.getVeiculo().getId());
        dto.setDataCriacao(venda.getDataCriacao());
        dto.setTotal(venda.getTotal());
        dto.setItens(venda.getItens().stream().map(this::itemParaResposta).collect(Collectors.toList()));
        return dto;
    }

    private ItemVendaRespostaDTO itemParaResposta(ItemVenda item) {
        ItemVendaRespostaDTO dto = new ItemVendaRespostaDTO();
        dto.setMercadoriaId(item.getMercadoria().getId());
        dto.setMercadoriaNome(item.getMercadoria().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
