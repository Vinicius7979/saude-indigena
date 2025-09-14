package com.saude_indigena.dto;

import com.saude_indigena.model.UserRole;
import com.saude_indigena.model.Usuario;
import java.util.UUID;

public record UsuarioListagemDTO(UUID uuid, String usuario, String password, UserRole role) {
    public UsuarioListagemDTO(Usuario usuario){
        this(usuario.getUuid(), usuario.getUsuario(), usuario.getPassword(), usuario.getRole());
    }
}
