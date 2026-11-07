package com.autobots.automanager.microservicos.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.autobots.automanager.configuracao.MicroservicosConfiguracao;

@SpringBootApplication
@Import(MicroservicosConfiguracao.class)
public class CatalogoApplication {
    public static void main(String[] args) { SpringApplication.run(CatalogoApplication.class, args); }
}
