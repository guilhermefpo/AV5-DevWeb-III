package com.autobots.automanager.servicos;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import javax.persistence.EntityNotFoundException;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.dtos.MercadoriaDTO;
import com.autobots.automanager.dtos.MercadoriaRespostaDTO;

@Service
public class MercadoriaServicos {
    @Autowired private MercadoriaRepositorio mercadoriaRepositorio;
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<MercadoriaRespostaDTO> listar() {
        return mercadoriaRepositorio.findAll().stream().map(e -> modelMapper.map(e, MercadoriaRespostaDTO.class)).collect(Collectors.toList());
    }

    public MercadoriaRespostaDTO buscar(Long id) {
        return modelMapper.map(mercadoriaRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Mercadoria não encontrada: " + id)), MercadoriaRespostaDTO.class);
    }

    public MercadoriaRespostaDTO salvar(Long id, MercadoriaDTO dto, Long empresaId) {
        Mercadoria e = id == null ? new Mercadoria() : mercadoriaRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mercadoria não encontrada: " + id));
        modelMapper.map(dto, e);
        if (empresaId != null) {
            Empresa empresa = empresaRepositorio.findById(empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
            e.setEmpresa(empresa);
        } else if (id == null) {
            throw new IllegalArgumentException("Informe empresaId para associar a mercadoria.");
        }
        return modelMapper.map(mercadoriaRepositorio.save(e), MercadoriaRespostaDTO.class);
    }

    public void excluir(Long id) {
        mercadoriaRepositorio.delete(mercadoriaRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Mercadoria não encontrada: " + id)));
    }
}
