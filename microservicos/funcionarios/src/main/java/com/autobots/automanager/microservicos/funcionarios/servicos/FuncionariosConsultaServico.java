package com.autobots.automanager.microservicos.funcionarios.servicos;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.autobots.automanager.dtos.UsuarioRespostaDTO;
import com.autobots.automanager.enumeracao.PerfilUsuario;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Service
public class FuncionariosConsultaServico {
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private ModelMapper modelMapper;

    public List<UsuarioRespostaDTO> listarPorEmpresa(Long empresaId) {
        empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
        return usuarioRepositorio.findByEmpresa_Id(empresaId).stream()
                .filter(usuario -> usuario.getPerfil() != PerfilUsuario.CLIENTE)
                .map(usuario -> {
                    UsuarioRespostaDTO dto = modelMapper.map(usuario, UsuarioRespostaDTO.class);
                    dto.setCredencial(null);
                    return dto;
                }).collect(Collectors.toList());
    }
}
