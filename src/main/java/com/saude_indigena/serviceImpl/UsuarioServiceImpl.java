package com.saude_indigena.serviceImpl;

import com.saude_indigena.dto.UsuarioAtualizacaoDTO;
import com.saude_indigena.dto.UsuarioListagemDTO;
import com.saude_indigena.excecoes.ObjetoNaoEncontradoException;
import com.saude_indigena.excecoes.ValidacaoException;
import com.saude_indigena.model.Admin;
import com.saude_indigena.model.UserRole;
import com.saude_indigena.model.Usuario;
import com.saude_indigena.repository.AdminRepository;
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
    private final AdminRepository adminRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, AdminRepository adminRepository) {
        this.usuarioRepository = usuarioRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional
    @Override
    public Usuario adicionar(Usuario usuario) throws DataIntegrityViolationException {
        try {
            this.validar(usuario);

            if (usuario.getRole() == UserRole.ADMIN) {
                log.info("Detectado role ADMIN");

                if (this.adminRepository.findByUsuario(usuario.getUsuario()) != null) {
                    log.error("Admin já existe: {}", usuario.getUsuario());
                    throw new ValidacaoException("Usuário já cadastrado no sistema");
                }

                String encryptedPassword = new BCryptPasswordEncoder().encode(usuario.getPassword());
                Admin admin = new Admin(usuario.getUsuario(), encryptedPassword, usuario.getRole());

                admin = this.adminRepository.save(admin);
                log.info(Constantes.USUARIO_MSG_ADICIONADO + " como ADMIN: {}", admin.getUsuario());

                usuario.setPassword(encryptedPassword);
                return usuario;

            } else {
                log.info("Detectado role USER");

                if (this.usuarioRepository.findByUsuario(usuario.getUsuario()) != null) {
                    log.error("Usuário já existe: {}", usuario.getUsuario());
                    throw new ValidacaoException("Usuário já cadastrado no sistema");
                }

                String encryptedPassword = new BCryptPasswordEncoder().encode(usuario.getPassword());
                usuario.setPassword(encryptedPassword);

                usuario = this.usuarioRepository.save(usuario);
                log.info(Constantes.USUARIO_MSG_ADICIONADO + ": {}", usuario.getUsuario());
                this.usuarioRepository.flush();
            }

        } catch (DataIntegrityViolationException e) {
            log.error(Constantes.USUARIO_MSG_FALHA_AO_ADICIONAR + ": " + e.getMessage());
            if (e.getMessage().contains("cpf")) {
                throw new ValidacaoException("CPF já cadastrado no sistema");
            } else if (e.getMessage().contains("usuario")) {
                throw new ValidacaoException("Nome de usuário já existe");
            }
            throw new ValidacaoException("Erro ao cadastrar usuário: dados duplicados");
        } catch (ValidacaoException e) {
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

            usuario.setNomeCompleto(dados.nomeCompleto());
            usuario.setCpf(dados.cpf());
            usuario.setDataNascimento(dados.dataNascimento());
            usuario.setEmail(dados.email());
            usuario.setTelefone(dados.telefone());
            usuario.setUsuario(dados.usuario());

            if (dados.password() != null && !dados.password().isEmpty()) {
                String encryptedPassword = new BCryptPasswordEncoder().encode(dados.password());
                usuario.setPassword(encryptedPassword);
                log.info("Senha atualizada para o usuário: {}", usuario.getUsuario());
            } else {
                log.info("Senha não foi alterada para o usuário: {}", usuario.getUsuario());
            }

            usuario.setCargo(dados.cargo());
            usuario.setRole(dados.role());

            this.usuarioRepository.save(usuario);
            log.info(Constantes.USUARIO_MSG_ATUALIZADO);
            return usuario;
        } catch (DataIntegrityViolationException e) {
            log.error(Constantes.USUARIO_MSG_FALHA_AO_ATUALIZAR + ": " + e.getMessage());
            throw new ValidacaoException("Erro ao atualizar usuário: dados duplicados");
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
        if (usuario.isEmpty()) {
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
            log.info(Constantes.USUARIO_MSG_REMOVIDA + ": {}", usuario.getUsuario());
        } catch (DataIntegrityViolationException e) {
            log.error(Constantes.USUARIO_MSG_FALHA_AO_REMOVER + ": {}", e.getMessage());
            throw e;
        }
    }

    private void validar(Usuario usuario) {
        if (usuario.getUsuario() == null || usuario.getUsuario().isEmpty() || usuario.getUsuario().isBlank()) {
            log.error("Campo 'usuário' inválido");
            throw new ValidacaoException("Campo 'usuário' é obrigatório");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isEmpty() || usuario.getPassword().isBlank()) {
            log.error("Campo 'senha' inválido");
            throw new ValidacaoException("Campo 'senha' é obrigatório");
        }

        if (usuario.getNomeCompleto() == null || usuario.getNomeCompleto().isEmpty() || usuario.getNomeCompleto().isBlank()) {
            log.error("Campo 'nome completo' inválido");
            throw new ValidacaoException("Campo 'nome completo' é obrigatório");
        }

        if (usuario.getCpf() == null || usuario.getCpf().isEmpty() || usuario.getCpf().isBlank()) {
            log.error("Campo 'CPF' inválido");
            throw new ValidacaoException("Campo 'CPF' é obrigatório");
        }

        if (usuario.getCpf().length() != 11) {
            log.error("CPF com tamanho inválido: {}", usuario.getCpf().length());
            throw new ValidacaoException("CPF deve ter exatamente 11 dígitos");
        }

        if (usuario.getDataNascimento() == null) {
            log.error("Campo 'data de nascimento' inválido");
            throw new ValidacaoException("Campo 'data de nascimento' é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isEmpty() || usuario.getEmail().isBlank()) {
            log.error("Campo 'email' inválido");
            throw new ValidacaoException("Campo 'email' é obrigatório");
        }

        if (!usuario.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            log.error("Email com formato inválido: {}", usuario.getEmail());
            throw new ValidacaoException("Email com formato inválido");
        }

        if (usuario.getRole() == null) {
            log.error("Campo 'role' inválido");
            throw new ValidacaoException("Campo 'perfil (role)' é obrigatório");
        }

        log.info("Validação do usuário '{}' concluída com sucesso", usuario.getUsuario());
    }

    private void validarAtualizacao(UsuarioAtualizacaoDTO dados) {
        if (dados.usuario() == null || dados.usuario().isEmpty() || dados.usuario().isBlank()) {
            log.error("Campo 'usuário' inválido na atualização");
            throw new ValidacaoException("Campo 'usuário' é obrigatório");
        }

        if (dados.nomeCompleto() == null || dados.nomeCompleto().isEmpty() || dados.nomeCompleto().isBlank()) {
            log.error("Campo 'nome completo' inválido na atualização");
            throw new ValidacaoException("Campo 'nome completo' é obrigatório");
        }

        if (dados.cpf() == null || dados.cpf().isEmpty() || dados.cpf().isBlank()) {
            log.error("Campo 'CPF' inválido na atualização");
            throw new ValidacaoException("Campo 'CPF' é obrigatório");
        }

        if (dados.email() == null || dados.email().isEmpty() || dados.email().isBlank()) {
            log.error("Campo 'email' inválido na atualização");
            throw new ValidacaoException("Campo 'email' é obrigatório");
        }

        if (dados.password() != null && !dados.password().isEmpty()) {
            if (dados.password().length() < 4) {
                log.error("Senha com tamanho inválido na atualização");
                throw new ValidacaoException("Senha deve ter pelo menos 4 caracteres");
            }
        }
    }
}