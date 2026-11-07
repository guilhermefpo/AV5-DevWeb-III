package com.autobots.automanager.microservicos.funcionarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.autobots.automanager.configuracao.MicroservicosConfiguracao;

@SpringBootApplication
@Import(MicroservicosConfiguracao.class)
public class FuncionariosApplication {
    public static void main(String[] args) { SpringApplication.run(FuncionariosApplication.class, args); }
}
