package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entidades.Servico;

public interface ServicoRepositorio extends JpaRepository<Servico, Long> {
    java.util.List<Servico> findByEmpresa_Id(Long empresaId);
}
