package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entidades.OrdemServico;

public interface OrdemServicoRepositorio extends JpaRepository<OrdemServico, Long> {
    java.util.List<OrdemServico> findByEmpresa_Id(Long empresaId);
    java.util.List<OrdemServico> findByEmpresa_IdAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
            Long empresaId, java.time.LocalDateTime inicio, java.time.LocalDateTime fim);
}
