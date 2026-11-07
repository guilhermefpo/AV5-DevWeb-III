package com.autobots.automanager.seguranca.infra.security;

import com.autobots.automanager.seguranca.infra.security.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigurations {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {

        httpSecurity.cors().and().csrf().disable();
        httpSecurity.formLogin().disable().httpBasic().disable();
        httpSecurity.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        httpSecurity.authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/empresas/**"))
                .hasAnyRole("ADMINISTRADOR", "GERENTE", "VENDEDOR")
                .requestMatchers(
                        new AntPathRequestMatcher("/servico/**", "GET"),
                        new AntPathRequestMatcher("/mercadoria/**", "GET"))
                .hasAnyRole("GERENTE", "VENDEDOR", "ADMINISTRADOR")
                .requestMatchers(
                        new AntPathRequestMatcher("/servico/**"),
                        new AntPathRequestMatcher("/mercadoria/**"))
                .hasAnyRole("GERENTE", "ADMINISTRADOR")
                .requestMatchers(new AntPathRequestMatcher("/usuario/**"))
                .hasAnyRole("ADMINISTRADOR", "GERENTE", "VENDEDOR", "CLIENTE")
                .requestMatchers(new AntPathRequestMatcher("/cliente/**", "GET"))
                .hasAnyRole("GERENTE", "VENDEDOR", "CLIENTE", "ADMINISTRADOR")
                .requestMatchers(new AntPathRequestMatcher("/cliente/**"))
                .hasAnyRole("GERENTE", "VENDEDOR", "ADMINISTRADOR")
                .requestMatchers(new AntPathRequestMatcher("/venda/**", "GET"))
                .hasAnyRole("GERENTE", "VENDEDOR", "CLIENTE", "ADMINISTRADOR")
                .requestMatchers(new AntPathRequestMatcher("/venda/**", "POST"))
                .hasAnyRole("GERENTE", "VENDEDOR", "ADMINISTRADOR")
                .requestMatchers(new AntPathRequestMatcher("/venda/**"))
                .hasAnyRole("GERENTE", "ADMINISTRADOR")
                .anyRequest().hasRole("ADMINISTRADOR");

        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(
                tokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
