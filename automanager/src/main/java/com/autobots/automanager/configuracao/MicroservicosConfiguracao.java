package com.autobots.automanager.configuracao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.bind.annotation.RestController;
import com.autobots.automanager.AutomanagerApplication;

@Configuration
@ComponentScan(basePackages = "com.autobots.automanager", excludeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = RestController.class),
        @Filter(type = FilterType.ANNOTATION, classes = SpringBootConfiguration.class),
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { AutomanagerApplication.class, MicroservicosConfiguracao.class })
})
@EntityScan("com.autobots.automanager.entidades")
@EnableJpaRepositories("com.autobots.automanager.repositorios")
public class MicroservicosConfiguracao {
}
