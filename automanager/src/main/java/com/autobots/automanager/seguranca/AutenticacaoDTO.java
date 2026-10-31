package com.autobots.automanager.seguranca;

import javax.validation.constraints.NotBlank;

public record AutenticacaoDTO(
        @NotBlank(message = "Login é obrigatório") String login,
        @NotBlank(message = "Senha é obrigatória") String senha) {
}
