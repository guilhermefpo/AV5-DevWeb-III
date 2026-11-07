package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entidades.Usuario;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCredencialLogin(String login);
    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Long id);
    List<Usuario> findByEmpresa_Id(Long empresaId);
}
