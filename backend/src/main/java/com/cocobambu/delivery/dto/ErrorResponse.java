package com.cocobambu.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Resposta padrao de erro da API")
public class ErrorResponse {

    @Schema(description = "Codigo HTTP do erro", example = "400")
    private int status;

    @Schema(description = "Tipo do erro", example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Transicao de status invalida de DELIVERED para RECEIVED")
    private String message;

    @Schema(description = "Timestamp do erro em milissegundos (epoch)", example = "1770842000000")
    private long timestamp;
}
