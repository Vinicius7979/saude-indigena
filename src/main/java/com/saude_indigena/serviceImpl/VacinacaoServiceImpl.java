package com.saude_indigena.serviceImpl;

import com.saude_indigena.dto.VacinacaoRegistroDTO;
import com.saude_indigena.excecoes.ValidacaoException;
import com.saude_indigena.model.Pessoa;
import com.saude_indigena.model.Vacina;
import com.saude_indigena.model.Vacinacao;
import com.saude_indigena.repository.VacinacaoRepository;
import com.saude_indigena.service.PessoaService;
import com.saude_indigena.service.VacinaService;
import com.saude_indigena.service.VacinacaoService;
import com.saude_indigena.util.Constantes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Slf4j
@Service
public class VacinacaoServiceImpl implements VacinacaoService {

    private final VacinacaoRepository vacinacaoRepository;

    private final PessoaService pessoaService;

    private final VacinaService vacinaService;

    public VacinacaoServiceImpl(VacinacaoRepository vacinacaoRepository, PessoaService pessoaService, VacinaService vacinaService) {
        this.vacinacaoRepository = vacinacaoRepository;
        this.pessoaService = pessoaService;
        this.vacinaService = vacinaService;
    }

    public Vacinacao registrar(VacinacaoRegistroDTO dados) throws DataIntegrityViolationException {
        Vacinacao vacinacao = new Vacinacao(dados);
        try {
            this.validar(dados);
            Pessoa pessoa = this.pessoaService.buscarPorUuid(dados.pessoaUuid());
            Vacina vacina = this.vacinaService.buscarPorUuid(dados.vacinaUuid());
            vacinacao.setPessoa(pessoa);
            vacinacao.setVacina(vacina);
            vacinacao.setDataAplicacao(LocalDate.now());
            vacinacao = this.vacinacaoRepository.save(vacinacao);
            log.info(Constantes.VACINACAO_MSG_REGISTRADO + ": {}", vacinacao);
        }catch (DataIntegrityViolationException e){
            log.error(Constantes.VACINACAO_MSG_REGISTRADO + ": " + e.getMessage());
            throw e;
        }
        return vacinacao;
    }

    private void validar(VacinacaoRegistroDTO dados) {
        if (dados.dataAplicacao() == null) {
            log.error(Constantes.VACINA_VALIDACAO_CAMPO_INVALIDO + ": {}", dados);
            throw new ValidacaoException(Constantes.PESSOA_VALIDACAO_CAMPO_INVALIDO);
        }
    }
}
