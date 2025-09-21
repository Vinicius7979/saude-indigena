package com.saude_indigena.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

public record VacinacaoRegistroDTO(@NotBlank UUID pessoaUuid, @NotBlank UUID vacinaUuid, LocalDate dataAplicacao, LocalDate dataProximaDose) {
}
