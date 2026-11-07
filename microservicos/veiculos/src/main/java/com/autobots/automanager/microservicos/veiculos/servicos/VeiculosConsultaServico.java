package com.autobots.automanager.microservicos.veiculos.servicos;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.autobots.automanager.dtos.VeiculoRespostaDTO;
import com.autobots.automanager.enumeracao.StatusOrdemServico;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.OrdemServicoRepositorio;

@Service
public class VeiculosConsultaServico {
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private OrdemServicoRepositorio ordemServicoRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<VeiculoRespostaDTO> listarAtendidosPorEmpresa(Long empresaId) {
        empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
        return ordemServicoRepositorio.findByEmpresa_Id(empresaId).stream()
                .filter(ordem -> ordem.getStatus() == StatusOrdemServico.CONCLUIDA)
                .map(ordem -> ordem.getVeiculo())
                .collect(Collectors.toMap(veiculo -> veiculo.getId(), veiculo -> veiculo,
                        (primeiro, repetido) -> primeiro))
                .values().stream().map(veiculo -> modelMapper.map(veiculo, VeiculoRespostaDTO.class))
                .collect(Collectors.toList());
    }
}
