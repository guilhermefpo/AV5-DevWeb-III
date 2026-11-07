package com.autobots.automanager.controles;

import javax.validation.Valid;

import com.autobots.automanager.seguranca.AutenticacaoDTO;
import com.autobots.automanager.seguranca.infra.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/auth")
public class AutenticacaoControler {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid AutenticacaoDTO data) {
        var credenciais = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var usuario = (UserDetails) authenticationManager.authenticate(credenciais).getPrincipal();
        return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(usuario), "Bearer"));
    }

    public record TokenDTO(String token, String tipo) {
    }
}
