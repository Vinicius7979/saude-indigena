package com.saude_indigena.serviceImpl;

import com.saude_indigena.repository.AdminRepository;
import com.saude_indigena.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class Authorizationservice implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final AdminRepository adminRepository;

    public Authorizationservice(UsuarioRepository usuarioRepository, AdminRepository adminRepository) {
        this.usuarioRepository = usuarioRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.matches("\\d+")) { // Verifica se NÃO é composto apenas por números
            UserDetails admin = adminRepository.findByUsuario(username);
            if (admin == null) {
                throw new UsernameNotFoundException("Admin não encontrado: " + username);
            }
            return admin;
        }
        else if (username.matches("\\d{6}")) { // Exatamente 6 dígitos
            UserDetails inspetor = usuarioRepository.findByUsuario(username);
            if (inspetor == null) {
                throw new UsernameNotFoundException("Inspetor não encontrado: " + username);
            }
            return inspetor;
        }
        // Caso não se encaixe em nenhum (formato inválido)
        throw new UsernameNotFoundException("Formato inválido. Use: 'usuário' (Admin) ou 'matrícula' (6 dígitos)");
    }
}
