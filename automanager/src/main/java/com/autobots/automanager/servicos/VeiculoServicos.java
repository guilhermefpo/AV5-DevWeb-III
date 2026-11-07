package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import javax.persistence.EntityNotFoundException;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.dtos.VeiculoDTO;
import com.autobots.automanager.dtos.VeiculoRespostaDTO;

@Service
@Transactional
public class VeiculoServicos {
    @Autowired private VeiculoRepositorio veiculoRepositorio;
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<VeiculoRespostaDTO> listar() {
        return veiculoRepositorio.findAll().stream().map(e -> modelMapper.map(e, VeiculoRespostaDTO.class)).collect(Collectors.toList());
    }

    public VeiculoRespostaDTO buscar(Long id) {
        return modelMapper.map(veiculoRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + id)), VeiculoRespostaDTO.class);
    }

    public VeiculoRespostaDTO salvar(Long id, VeiculoDTO dto, Long usuarioId) {
        Veiculo e = id == null ? new Veiculo() : veiculoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + id));
        modelMapper.map(dto, e);
        if (e.getPlaca() != null && (e.getId() == null ? veiculoRepositorio.existsByPlaca(e.getPlaca())
                : veiculoRepositorio.existsByPlacaAndIdNot(e.getPlaca(), e.getId()))) {
            throw new org.springframework.dao.DataIntegrityViolationException("Placa já cadastrada.");
        }
        if (usuarioId != null) {
            Usuario usuario = usuarioRepositorio.findById(usuarioId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usuarioId));
            e.setUsuario(usuario);
        } else if (id == null) {
            throw new IllegalArgumentException("Informe usuarioId para associar o veículo.");
        }
        return modelMapper.map(veiculoRepositorio.save(e), VeiculoRespostaDTO.class);
    }

    public void excluir(Long id) {
        veiculoRepositorio.delete(veiculoRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + id)));
    }
}
