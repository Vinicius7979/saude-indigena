package com.saude_indigena.serviceImpl;

import com.saude_indigena.dto.VacinaAtualizacaoDTO;
import com.saude_indigena.dto.VacinaListagemDTO;
import com.saude_indigena.excecoes.ObjetoNaoEncontradoException;
import com.saude_indigena.excecoes.ValidacaoException;
import com.saude_indigena.model.Vacina;
import com.saude_indigena.repository.VacinaRepository;
import com.saude_indigena.service.VacinaService;
import com.saude_indigena.util.Constantes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VacinaServiceimpl implements VacinaService {

    private final VacinaRepository vacinaRepository;

    public VacinaServiceimpl(VacinaRepository vacinaRepository) {
        this.vacinaRepository = vacinaRepository;
    }

    @Transactional
    @Override
    public Vacina adicionar(Vacina vacina) throws DataIntegrityViolationException {
        try {
            vacina = this.vacinaRepository.save(vacina);
            log.info(Constantes.VACINA_MSG_ADICIONADA + ": {}", vacina);
        }catch (DataIntegrityViolationException e) {
            log.error(Constantes.VACINA_MSG_ADICIONADA + ": " + e.getMessage());
            throw e;
        }
        return vacina;
    }

    @Override
    public Vacina atualizar(UUID vacinaUuid, VacinaAtualizacaoDTO dados) {
        try {
            Vacina vacina = this.buscarPorUuid(vacinaUuid);
            if (vacina.isAtivo()){
                vacina.setNome(dados.nome());
                vacina.setNumeroLote(dados.numeroLote());
                vacina.setDataFabricacao(dados.dataFabricacao());
                vacina.setDataValidade(dados.dataValidade());
                vacina.setFabricante(dados.fabricante());
                this.vacinaRepository.save(vacina);
                log.info(Constantes.VACINA_MSG_ATUALIZADA);
                return vacina;
            }else{
                log.error(Constantes.VACINA_MSG_FALHA_AO_ATUALIZAR + ": Vacina {} inativa", vacina.getNome());
                throw new ObjetoNaoEncontradoException(Constantes.VACINA_MSG_FALHA_AO_ATUALIZAR);
            }
        }catch (DataIntegrityViolationException e){
            log.error(Constantes.VACINA_MSG_FALHA_AO_ATUALIZAR + ": {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<VacinaListagemDTO> listar(Pageable pageable) {
        return List.of();
    }

    @Override
    public List<VacinaListagemDTO> listarTodos(Pageable pageable) {
        return List.of();
    }

    @Override
    public Vacina buscarPorUuid(UUID vacinaUuid) throws ObjetoNaoEncontradoException {
        return null;
    }

    @Override
    public void remover(UUID vacinaUuid) {

    }

    private void validar(Vacina pessoa) {
        if (pessoa.getNome() == null || pessoa.getNumeroLote() == null || pessoa.getDataFabricacao() == null || pessoa.getDataValidade() == null || pessoa.getFabricante() == null) {
            log.error(Constantes.VACINA_VALIDACAO_CAMPO_INVALIDO + ": {}", pessoa);
            throw new ValidacaoException(Constantes.PESSOA_VALIDACAO_CAMPO_INVALIDO);
        }
        if (pessoa.getNome().isEmpty() || pessoa.getNome().isBlank() || pessoa.getNumeroLote().isEmpty() || pessoa.getNumeroLote().isBlank()) {
            log.warn(Constantes.VACINA_MSG_FALHA_AO_VALIDAR + ": {}", pessoa);
            throw new ValidacaoException(Constantes.PESSOA_VALIDACAO_CAMPO_INVALIDO);
        }
    }

    private void validarAtualizacao(VacinaAtualizacaoDTO dados) {
        if (dados.nome() == null || dados.numeroLote() == null || dados.dataFabricacao() == null || dados.dataValidade() == null || dados.fabricante() == null) {
            log.error(Constantes.VACINA_VALIDACAO_CAMPO_INVALIDO + ": {}", dados);
            throw new ValidacaoException(Constantes.PESSOA_VALIDACAO_CAMPO_INVALIDO);
        }
        if (dados.nome().isEmpty() || dados.nome().isBlank() || dados.numeroLote().isEmpty() || dados.numeroLote().isBlank()) {
            log.warn(Constantes.VACINA_MSG_FALHA_AO_VALIDAR + ": {}", dados);
            throw new ValidacaoException(Constantes.VACINA_VALIDACAO_CAMPO_INVALIDO);
        }
    }
}
