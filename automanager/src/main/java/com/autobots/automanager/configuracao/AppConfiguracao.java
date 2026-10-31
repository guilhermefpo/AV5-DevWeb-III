package com.autobots.automanager.configuracao;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.enumeracao.PerfilUsuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Configuration
public class AppConfiguracao {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner criarAdministradorInicial(
            UsuarioRepositorio usuarios,
            PasswordEncoder passwordEncoder,
            Environment environment) {
        return args -> {
            String login = environment.getProperty("app.bootstrap-admin.login", " ");
            String senha = environment.getProperty("app.bootstrap-admin.senha", " ");
            if (login.isBlank() && senha.isBlank()) return;
            if (login.isBlank() || senha.isBlank()) {
                throw new IllegalStateException("Configure login e senha para criar o administrador inicial.");
            }
            boolean existeAdministrador = usuarios.findAll().stream()
                    .anyMatch(usuario -> usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR);
            if (existeAdministrador) return;

            Credencial credencial = new Credencial();
            credencial.setLogin(login);
            credencial.setSenha(passwordEncoder.encode(senha));

            Usuario administrador = new Usuario();
            administrador.setNome("Administrador inicial");
            administrador.setPerfil(PerfilUsuario.ADMINISTRADOR);
            administrador.setCredencial(credencial);
            usuarios.save(administrador);
        };
    }
}
