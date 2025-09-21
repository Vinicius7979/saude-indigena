package com.saude_indigena.controller;

import com.saude_indigena.dto.VacinacaoRegistroDTO;
import com.saude_indigena.dto.VacinacaoResponseDTO;
import com.saude_indigena.model.Vacinacao;
import com.saude_indigena.model.mapper.VacinacaoMapper;
import com.saude_indigena.service.VacinacaoService;
import com.saude_indigena.util.ResponseApi;
import com.saude_indigena.util.TipoResponseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/vacinacoes")
public class VacinacaoController {

    private final VacinacaoService vacinacaoService;

    private final VacinacaoMapper vacinacaoMapper;

    public VacinacaoController(VacinacaoService vacinacaoService, VacinacaoMapper vacinacaoMapper) {
        this.vacinacaoService = vacinacaoService;
        this.vacinacaoMapper = vacinacaoMapper;
    }

    @Operation(summary = "Registra uma vacinação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vacinação registrada.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseApi.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "403", description = "Acesso proibido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @PostMapping
    public ResponseEntity<Object> adicionar(@RequestBody VacinacaoRegistroDTO dados){
        Vacinacao vacinacao = this.vacinacaoService.registrar(dados);
        VacinacaoResponseDTO response = this.vacinacaoMapper.toVacinacaoResponseDTO(vacinacao);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/").buildAndExpand(vacinacao.getId()).toUri();
        return ResponseApi.crudResponse(TipoResponseApi.INFO, "vacinacao registrada com sucesso", HttpStatus.CREATED, response, uri);
    }
}
