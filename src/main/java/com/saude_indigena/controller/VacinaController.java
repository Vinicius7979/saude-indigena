package com.saude_indigena.controller;

import com.saude_indigena.dto.VacinaCadastroDTO;
import com.saude_indigena.dto.VacinaResponseDTO;
import com.saude_indigena.model.Vacina;
import com.saude_indigena.model.mapper.VacinaMapper;
import com.saude_indigena.service.VacinaService;
import com.saude_indigena.util.Constantes;
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
@RequestMapping("/vacina")
public class VacinaController {

    private final VacinaService vacinaService;

    private final VacinaMapper vacinaMapper;

    public VacinaController(VacinaService vacinaService, VacinaMapper vacinaMapper) {
        this.vacinaService = vacinaService;
        this.vacinaMapper = vacinaMapper;
    }

    @Operation(summary = "Adiciona uma vacina.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pessoa adicionada.",
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
    public ResponseEntity<Object> adicionar(@RequestBody VacinaCadastroDTO dados){
        Vacina vacina = this.vacinaService.adicionar(new Vacina(dados));
        VacinaResponseDTO response = this.vacinaMapper.toVacinaResponseDTO(vacina);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/").buildAndExpand(vacina.getUuid()).toUri();
        return ResponseApi.crudResponse(TipoResponseApi.INFO, Constantes.VACINA_MSG_ADICIONADA, HttpStatus.CREATED, response, uri);
    }
}
