package com.saude_indigena.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.saude_indigena.repository.AdminRepository;
import com.saude_indigena.repository.UsuarioRepository;
import com.saude_indigena.serviceImpl.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${api.security.token.secret}")
    private String secret;

    public SecurityFilter(TokenService tokenService, AdminRepository adminRepository, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (token != null) {
            try {
                DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret))
                        .withIssuer("auth-api")
                        .build()
                        .verify(token);

                String subject = jwt.getSubject();
                String role = jwt.getClaim("role").asString();

                log.info("Token validado - Usuário: " + subject + " | Role: " + role);

                UserDetails userDetails = null;

                userDetails = adminRepository.findByUsuario(subject);

                if (userDetails != null) {
                    log.info("Encontrado na tabela ADMIN: " + subject);
                } else {
                    userDetails = usuarioRepository.findByUsuario(subject);

                    if (userDetails != null) {
                        log.info("Encontrado na tabela USUARIO: " + subject);
                    } else {
                        log.error("Usuário não encontrado em nenhuma tabela: " + subject);
                    }
                }

                if (userDetails == null) {
                    log.error("UserDetails é nulo - rejeitando requisição");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado");
                    return;
                }

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Autenticação configurada com authorities: " + userDetails.getAuthorities());

            } catch (JWTVerificationException e) {
                log.error("Token inválido: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            } catch (Exception e) {
                log.error("Erro ao processar token: " + e.getMessage());
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}