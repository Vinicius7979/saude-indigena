package com.saude_indigena.serviceImpl;

import com.saude_indigena.dto.UsuarioAtualizacaoDTO;
import com.saude_indigena.dto.UsuarioListagemDTO;
import com.saude_indigena.excecoes.ObjetoNaoEncontradoException;
import com.saude_indigena.excecoes.ValidacaoException;
import com.saude_indigena.model.Usuario;
import com.saude_indigena.repository.UsuarioRepository;
import com.saude_indigena.service.UsuarioService;
import com.saude_indigena.util.Constantes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    @Override
    public Usuario adicionar(Usuario usuario) throws DataIntegrityViolationException {
        try {
            this.validar(usuario);
            if (this.usuarioRepository.findByUsuario(usuario.getUsuario()) != null) {
                throw new ValidacaoException(Constantes.USUARIO_MSG_FALHA_AO_VALIDAR);
            }

            String encryptedPassword = new BCryptPasswordEncoder().encode(usuario.getPassword());
            usuario.setPassword(encryptedPassword);

            usuario = this.usuarioRepository.save(usuario);
            log.info(Constantes.USUARIO_MSG_ADICIONADO + ": {}", usuario);
            this.usuarioRepository.flush();
        }catch (DataIntegrityViolationException e) {
            log.error(Constantes.USUARIO_MSG_FALHA_AO_ADICIONAR + ": " + e.getMessage());
            throw e;
        }
        return usuario;
    }

    @Transactional
    @Override
    public Usuario atualizar(UUID usuarioUuid, UsuarioAtualizacaoDTO dados) {
        try {
            Usuario usuario = this.buscarPorUuid(usuarioUuid);
            this.validarAtualizacao(dados);
            usuario.setUsuario(dados.usuario());
            usuario.setPassword(dados.password());
            this.usuarioRepository.save(usuario);
            log.info(Constantes.USUARIO_MSG_ATUALIZADO);
            return usuario;
        }catch (DataIntegrityViolationException e){
            log.error(Constantes.USUARIO_MSG_FALHA_AO_ATUALIZAR + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<UsuarioListagemDTO> listar(Pageable pageable) {
        List<Usuario> lista = this.usuarioRepository.listar(pageable);
        return lista.stream().map(UsuarioListagemDTO::new).toList();
    }

    @Override
    public Usuario buscarPorUuid(UUID usuarioUuid) throws ObjetoNaoEncontradoException {
        Optional<Usuario> usuario = this.usuarioRepository.buscarPorUuid(usuarioUuid);
        if (usuario.isEmpty()){
            log.warn(Constantes.USUARIO_MSG_NAO_LOCALIZADA + ": {}", usuarioUuid);
            throw new ObjetoNaoEncontradoException(Constantes.USUARIO_MSG_NAO_LOCALIZADA);
        }
        return usuario.get();
    }

    @Transactional
    @Override
    public void remover(UUID usuarioUuid) {
        try {
            Usuario usuario = this.buscarPorUuid(usuarioUuid);
            usuario.setRemovedAt(OffsetDateTime.now());
            this.usuarioRepository.delete(usuario);
            log.info(Constantes.USUARIO_MSG_REMOVIDA + ": {}", usuario);
        }catch (DataIntegrityViolationException e){
            log.error(Constantes.USUARIO_MSG_FALHA_AO_REMOVER + ": {}", e.getMessage());
            throw e;
        }
    }

    private void validar(Usuario usuario){
        if (usuario.getUsuario() == null || usuario.getPassword() == null){
            log.error(Constantes.USUARIO_MSG_VALIDACAO_CAMPO_INVALIDO + usuario);
            throw new ValidacaoException(Constantes.USUARIO_MSG_VALIDACAO_CAMPO_INVALIDO);
        }
        if (usuario.getUsuario().isBlank() || usuario.getUsuario().isEmpty() || usuario.getPassword().isEmpty() || usuario.getPassword().isBlank()){
            log.warn(Constantes.USUARIO_MSG_FALHA_AO_VALIDAR + ": " + usuario);
            throw new ValidacaoException(Constantes.USUARIO_MSG_VALIDACAO_CAMPO_INVALIDO);
        }
    }

    private void validarAtualizacao(UsuarioAtualizacaoDTO dados){
        if (dados.usuario() == null || dados.password() == null){
            log.error(Constantes.USUARIO_MSG_VALIDACAO_CAMPO_INVALIDO + dados);
            throw new ValidacaoException(Constantes.USUARIO_MSG_VALIDACAO_CAMPO_INVALIDO);
        }
        if (dados.usuario().isBlank() || dados.usuario().isEmpty() || dados.password().isEmpty() || dados.password().isBlank()){
            log.warn(Constantes.USUARIO_MSG_FALHA_AO_VALIDAR + ": " + dados);
            throw new ValidacaoException(Constantes.USUARIO_MSG_VALIDACAO_CAMPO_INVALIDO);
        }
    }
}
