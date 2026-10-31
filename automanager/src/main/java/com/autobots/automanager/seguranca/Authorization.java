package com.autobots.automanager.seguranca;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Service
public class Authorization implements UserDetailsService {
    private final UsuarioRepositorio usuarios;

    public Authorization(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarios.findByCredencialLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        if (usuario.getCredencial() == null || usuario.getPerfil() == null) {
            throw new UsernameNotFoundException("Usuário sem credencial ou perfil válido: " + login);
        }

        return new UsuarioDetails(usuario);
    }
}
