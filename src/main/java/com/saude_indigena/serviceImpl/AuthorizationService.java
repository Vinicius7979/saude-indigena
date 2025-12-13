package com.saude_indigena.serviceImpl;

import com.saude_indigena.repository.AdminRepository;
import com.saude_indigena.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthorizationService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;

    public AuthorizationService(AdminRepository adminRepository, UsuarioRepository usuarioRepository) {
        this.adminRepository = adminRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Buscando usuário: " + username);

        UserDetails user = usuarioRepository.findByUsuario(username);
        if (user != null) {
            log.info("Encontrado como USUARIO: " + username);
            return user;
        }

        UserDetails admin = adminRepository.findByUsuario(username);
        if (admin != null) {
            log.info("Encontrado como ADMIN: " + username);
            return admin;
        }

        log.warn("Usuário não encontrado: " + username);
        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
}