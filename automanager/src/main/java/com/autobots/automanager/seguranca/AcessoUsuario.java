package com.autobots.automanager.seguranca;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.enumeracao.PerfilUsuario;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Component("acessoUsuario")
public class AcessoUsuario {
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private VendaRepositorio vendaRepositorio;
    @Autowired private ClienteRepositorio clienteRepositorio;

    public boolean podeLerUsuario(Long id) {
        if (!autenticado() || id == null) return false;
        if (temPerfil(PerfilUsuario.ADMINISTRADOR)) return true;
        return usuarioRepositorio.findById(id).map(usuario ->
                perfisPermitidos().contains(usuario.getPerfil()) || eUsuarioAtual(id)).orElse(false);
    }

    public boolean podeCriarUsuario(PerfilUsuario perfil) {
        return perfil != null && (temPerfil(PerfilUsuario.ADMINISTRADOR)
                || perfisPermitidos().contains(perfil));
    }

    public boolean podeAlterarUsuario(Long id, PerfilUsuario novoPerfil) {
        if (!autenticado() || id == null || novoPerfil == null) return false;
        if (temPerfil(PerfilUsuario.ADMINISTRADOR)) return true;
        return usuarioRepositorio.findById(id).map(usuario ->
                perfisPermitidos().contains(usuario.getPerfil()) && perfisPermitidos().contains(novoPerfil))
                .orElse(false);
    }

    public boolean podeExcluirUsuario(Long id) {
        if (!autenticado() || id == null) return false;
        if (temPerfil(PerfilUsuario.ADMINISTRADOR)) return true;
        return usuarioRepositorio.findById(id)
                .map(usuario -> perfisPermitidos().contains(usuario.getPerfil())).orElse(false);
    }

    public boolean eUsuarioAtual(Long id) {
        return id != null && usuarioRepositorio.findByCredencialLogin(loginAtual())
                .map(usuario -> id.equals(usuario.getId())).orElse(false);
    }

    public Long idUsuarioAtual() {
        return usuarioRepositorio.findByCredencialLogin(loginAtual()).map(Usuario::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado."));
    }

    public String loginAtualPublico() {
        return loginAtual();
    }

    public String documentoAtual() {
        return usuarioRepositorio.findByCredencialLogin(loginAtual()).map(Usuario::getDocumento).orElse(null);
    }

    public boolean podeLerCliente(Long clienteId) {
        if (temPerfil(PerfilUsuario.ADMINISTRADOR) || temPerfil(PerfilUsuario.GERENTE)
                || temPerfil(PerfilUsuario.VENDEDOR)) return true;
        if (!temPerfil(PerfilUsuario.CLIENTE) || clienteId == null) return false;
        String documento = documentoAtual();
        if (documento == null) return false;
        return clienteRepositorio.findById(clienteId).map(cliente -> documento.equals(cliente.getCpf())).orElse(false);
    }

    public boolean podeAcessarVenda(Long vendaId) {
        if (temPerfil(PerfilUsuario.ADMINISTRADOR) || temPerfil(PerfilUsuario.GERENTE)) return true;
        if (vendaId == null) return false;
        return vendaRepositorio.findById(vendaId).map(venda -> {
            if (temPerfil(PerfilUsuario.VENDEDOR)) {
                return venda.getCriadoPor() != null && eUsuarioAtual(venda.getCriadoPor().getId());
            }
            return temPerfil(PerfilUsuario.CLIENTE) && eUsuarioAtual(venda.getUsuario().getId());
        }).orElse(false);
    }

    public boolean podeVerTodasVendas() {
        return temPerfil(PerfilUsuario.ADMINISTRADOR) || temPerfil(PerfilUsuario.GERENTE);
    }

    public boolean eVendedor() {
        return temPerfil(PerfilUsuario.VENDEDOR);
    }

    public boolean ehCliente() {
        return temPerfil(PerfilUsuario.CLIENTE);
    }

    public Set<PerfilUsuario> perfisVisiveis() {
        if (temPerfil(PerfilUsuario.ADMINISTRADOR)) return EnumSet.allOf(PerfilUsuario.class);
        return perfisPermitidos();
    }

    private Set<PerfilUsuario> perfisPermitidos() {
        if (temPerfil(PerfilUsuario.GERENTE)) {
            return Set.of(PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR, PerfilUsuario.CLIENTE);
        }
        if (temPerfil(PerfilUsuario.VENDEDOR)) return Collections.singleton(PerfilUsuario.CLIENTE);
        return Collections.emptySet();
    }

    private boolean autenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    private String loginAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "" : auth.getName();
    }

    private boolean temPerfil(PerfilUsuario perfil) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + perfil.name()));
    }
}
