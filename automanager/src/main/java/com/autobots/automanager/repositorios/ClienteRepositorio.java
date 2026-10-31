package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Cliente;
import java.util.List;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
    boolean existsByCpf(String cpf);
    List<Cliente> findByCpf(String cpf);
}
