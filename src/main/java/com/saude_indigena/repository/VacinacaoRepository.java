package com.saude_indigena.repository;

import com.saude_indigena.model.Vacinacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacinacaoRepository extends JpaRepository<Vacinacao, UUID> {

    Optional<Vacinacao> findbyUuid(UUID vacinacaoUuid);

    List<Vacinacao> findByPessoaUuid(UUID pessoaUuid);
}
