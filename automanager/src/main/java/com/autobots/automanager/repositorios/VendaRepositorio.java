package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entidades.Venda;
import java.util.List;

public interface VendaRepositorio extends JpaRepository<Venda, Long> {
    List<Venda> findByUsuario_Credencial_Login(String login);
    List<Venda> findByCriadoPor_Credencial_Login(String login);
}
