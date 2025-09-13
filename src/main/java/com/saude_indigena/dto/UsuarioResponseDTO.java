package com.saude_indigena.dto;

import com.saude_indigena.model.UserRole;
import java.util.UUID;

public record UsuarioResponseDTO(UUID uuid, String usuario, String password, UserRole role, Boolean ativo) {
}
