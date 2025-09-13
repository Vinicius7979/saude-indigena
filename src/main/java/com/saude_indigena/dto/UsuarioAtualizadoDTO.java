package com.saude_indigena.dto;

import com.saude_indigena.model.UserRole;
import jakarta.validation.constraints.NotBlank;

public record UsuarioAtualizadoDTO(@NotBlank String usuario, @NotBlank String password, UserRole role, @NotBlank Boolean ativo) {
}
