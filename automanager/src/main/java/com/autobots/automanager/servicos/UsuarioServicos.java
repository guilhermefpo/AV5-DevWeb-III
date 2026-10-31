package com.autobots.automanager.servicos;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
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
    private final UsuarioRepositorio r;
    private final EmpresaRepositorio empresas;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AcessoUsuario acessoUsuario;

    public UsuarioServicos(UsuarioRepositorio r, EmpresaRepositorio empresas, ModelMapper mapper,
            PasswordEncoder passwordEncoder, AcessoUsuario acessoUsuario) {
        this.r = r;
        this.empresas = empresas;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.acessoUsuario = acessoUsuario;
    }

    public List<UsuarioRespostaDTO> listar() {
        Set<PerfilUsuario> perfisVisiveis = acessoUsuario.perfisVisiveis();
        Long usuarioAtualId = acessoUsuario.idUsuarioAtual();
        return r.findAll().stream()
                .filter(usuario -> perfisVisiveis.contains(usuario.getPerfil())
                        || usuarioAtualId.equals(usuario.getId()))
                .map(e -> mapper.map(e, UsuarioRespostaDTO.class)).collect(Collectors.toList());
    }

    public UsuarioRespostaDTO buscar(Long id) {
        return mapper.map(
                r.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id)),
                UsuarioRespostaDTO.class);
    }

    public UsuarioRespostaDTO salvar(Long id, UsuarioDTO dto, Long empresaId) {
        Usuario e = id == null ? new Usuario()
                : r.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
        mapper.map(dto, e);
        if (e.getCredencial() != null && e.getCredencial().getSenha() != null
                && !e.getCredencial().getSenha().matches("^\\$2[aby]\\$\\d{2}\\$.*")) {
            e.getCredencial().setSenha(passwordEncoder.encode(e.getCredencial().getSenha()));
        }
        if (e.getDocumento() != null && (e.getId() == null ? r.existsByDocumento(e.getDocumento())
                : r.existsByDocumentoAndIdNot(e.getDocumento(), e.getId()))) {
            throw new org.springframework.dao.DataIntegrityViolationException("Documento já cadastrado.");
        }
        if (empresaId != null) {
            Empresa empresa = empresas.findById(empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + empresaId));
            e.setEmpresa(empresa);
        } else if (id == null) {
            e.setEmpresa(null);
        }
        return mapper.map(r.save(e), UsuarioRespostaDTO.class);
    }

    public void excluir(Long id) {
        r.delete(r.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id)));
    }
}
