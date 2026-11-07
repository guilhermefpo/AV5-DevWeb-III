package com.autobots.automanager.microservicos.catalogo.servicos;

import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.autobots.automanager.dtos.CatalogoRespostaDTO;
import com.autobots.automanager.dtos.MercadoriaRespostaDTO;
import com.autobots.automanager.dtos.ServicoRespostaDTO;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.ServicoRepositorio;

@Service
public class CatalogoConsultaServico {
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private MercadoriaRepositorio mercadoriaRepositorio;
    @Autowired private ServicoRepositorio servicoRepositorio;
    @Autowired private ModelMapper modelMapper;

    public CatalogoRespostaDTO listarPorEmpresa(Long empresaId) {
        empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
        return new CatalogoRespostaDTO(
                mercadoriaRepositorio.findByEmpresa_IdAndEstoqueGreaterThan(empresaId, 0).stream()
                        .map(item -> modelMapper.map(item, MercadoriaRespostaDTO.class)).collect(Collectors.toList()),
                servicoRepositorio.findByEmpresa_Id(empresaId).stream()
                        .map(servico -> modelMapper.map(servico, ServicoRespostaDTO.class)).collect(Collectors.toList()));
    }
}
