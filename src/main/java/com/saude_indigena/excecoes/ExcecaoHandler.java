package com.saude_indigena.excecoes;

import com.saude_indigena.util.ResponseApi;
import com.saude_indigena.util.TipoResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExcecaoHandler {

    @ExceptionHandler(ObjetoNaoEncontradoException.class)
    public ResponseEntity<Object> objetoNaoEncontrado(ObjetoNaoEncontradoException exception, HttpServletRequest request) {
        return ResponseApi.erroResponse(TipoResponseApi.WARNING, exception.getMessage(), HttpStatus.NOT_FOUND, null, request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> violacaoIntegridade(DataIntegrityViolationException exception, HttpServletRequest request) {
        String mensagem = "Erro na requisição";
        if (exception.getMessage().contains("duplicate key")) {
            mensagem = "Registro em duplicidade";
        }
        return ResponseApi.erroResponse(TipoResponseApi.ERROR, mensagem, HttpStatus.BAD_REQUEST, "Dados inválidos", request.getRequestURI());
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Object> validacao(ValidacaoException exception, HttpServletRequest request) {
        return ResponseApi.erroResponse(TipoResponseApi.WARNING, "Falha de validação", HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> nulo(NullPointerException exception, HttpServletRequest request) {
        return ResponseApi.erroResponse(TipoResponseApi.ERROR, "Erro de valor nulo", HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> excecao(Exception exception, HttpServletRequest request) {
        log.error("Falha ao processar a requisição. " + exception.getMessage() + " ao chamar a URL " + request.getRequestURI());
        return ResponseApi.erroResponse(TipoResponseApi.ERROR, "Falha ao processar a requisição. Entre em contato com o suporte.", HttpStatus.INTERNAL_SERVER_ERROR, null, request.getRequestURI());
    }
}
