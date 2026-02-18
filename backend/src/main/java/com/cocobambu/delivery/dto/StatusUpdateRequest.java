package com.cocobambu.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Requisicao para atualizar o status de um pedido")
public class StatusUpdateRequest {

    @Schema(
            description = "Status alvo do pedido. Valores validos: RECEIVED, CONFIRMED, DISPATCHED, DELIVERED, CANCELED",
            example = "CONFIRMED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;
}
