package com.autobots.automanager.servicos;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import javax.persistence.EntityNotFoundException;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.dtos.UsuarioDTO;
import com.autobots.automanager.dtos.UsuarioRespostaDTO;
import com.autobots.automanager.seguranca.AcessoUsuario;
import com.autobots.automanager.enumeracao.PerfilUsuario;

@Service
@Transactional
public class UsuarioServicos {
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private EmpresaRepositorio empresaRepositorio;
    @Autowired private ModelMapper modelMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AcessoUsuario acessoUsuario;

    public List<UsuarioRespostaDTO> listar() {
        Set<PerfilUsuario> perfisVisiveis = acessoUsuario.perfisVisiveis();
        Long usuarioAtualId = acessoUsuario.idUsuarioAtual();
        return usuarioRepositorio.findAll().stream()
                .filter(usuario -> perfisVisiveis.contains(usuario.getPerfil())
                        || usuarioAtualId.equals(usuario.getId()))
                .map(e -> modelMapper.map(e, UsuarioRespostaDTO.class)).collect(Collectors.toList());
    }

    public UsuarioRespostaDTO buscar(Long id) {
        return modelMapper.map(
                usuarioRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id)),
                UsuarioRespostaDTO.class);
    }

    public UsuarioRespostaDTO salvar(Long id, UsuarioDTO dto, Long empresaId) {
        Usuario e = id == null ? new Usuario()
                : usuarioRepositorio.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
        modelMapper.map(dto, e);
        if (id == null) {
            if (e.getEndereco() != null) e.getEndereco().setId(null);
            if (e.getDocumentos() != null) e.getDocumentos().forEach(documento -> documento.setId(null));
            if (e.getTelefones() != null) e.getTelefones().forEach(telefone -> telefone.setId(null));
        }
        if (e.getCredencial() != null && e.getCredencial().getSenha() != null
                && !e.getCredencial().getSenha().matches("^\\$2[aby]\\$\\d{2}\\$.*")) {
            e.getCredencial().setSenha(passwordEncoder.encode(e.getCredencial().getSenha()));
        }
        if (e.getDocumento() != null && (e.getId() == null ? usuarioRepositorio.existsByDocumento(e.getDocumento())
                : usuarioRepositorio.existsByDocumentoAndIdNot(e.getDocumento(), e.getId()))) {
            throw new org.springframework.dao.DataIntegrityViolationException("Documento já cadastrado.");
        }
        if (empresaId == null && (e.getPerfil() == PerfilUsuario.GERENTE || e.getPerfil() == PerfilUsuario.VENDEDOR)) {
            throw new IllegalArgumentException("Informe empresaId para associar o funcionário à loja.");
        }
        if (empresaId != null) {
            Empresa empresa = empresaRepositorio.findById(empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
            e.setEmpresa(empresa);
        } else if (id == null) {
            e.setEmpresa(null);
        }
        return modelMapper.map(usuarioRepositorio.save(e), UsuarioRespostaDTO.class);
    }

    public void excluir(Long id) {
        usuarioRepositorio.delete(usuarioRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id)));
    }
}
