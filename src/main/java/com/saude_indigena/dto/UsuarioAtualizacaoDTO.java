package com.saude_indigena.dto;

import com.saude_indigena.model.UserRole;
import jakarta.validation.constraints.NotBlank;

public record UsuarioAtualizacaoDTO(@NotBlank String usuario, @NotBlank String password, UserRole role, @NotBlank Boolean ativo) {
}
