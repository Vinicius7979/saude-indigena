package com.saude_indigena.service;

import com.saude_indigena.dto.VacinacaoRegistroDTO;
import com.saude_indigena.model.Vacinacao;
import org.springframework.dao.DataIntegrityViolationException;

public interface VacinacaoService {

    Vacinacao registrar(VacinacaoRegistroDTO dados)throws DataIntegrityViolationException;
}