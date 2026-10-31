package com.autobots.automanager.seguranca.infra.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.autobots.automanager.seguranca.Authorization;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    
    private final TokenService tokenService;
    private final Authorization authorization;

    public TokenAuthenticationFilter( TokenService tokenService, Authorization authorization) {
        this.tokenService = tokenService;
        this.authorization = authorization;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
            String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ") 
            && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            String username = tokenService.getSubject(token);

            if (username != null) {
                try {
                    UserDetails userDetails = authorization.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails( new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                } catch (UsernameNotFoundException exception) {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        chain.doFilter(request, response);
    }
}
