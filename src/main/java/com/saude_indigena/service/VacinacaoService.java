package com.saude_indigena.service;

import com.saude_indigena.dto.VacinacaoListagemDTO;
import com.saude_indigena.dto.VacinacaoRegistroDTO;
import com.saude_indigena.model.Vacinacao;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface VacinacaoService {

    Vacinacao registrar(VacinacaoRegistroDTO dados)throws DataIntegrityViolationException;

    List<VacinacaoListagemDTO> listar(Pageable pageable);
}