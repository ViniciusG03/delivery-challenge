package com.cocobambu.delivery.controller;

import com.cocobambu.delivery.dto.ErrorResponse;
import com.cocobambu.delivery.dto.StatusUpdateRequest;
import com.cocobambu.delivery.model.OrderWrapper;
import com.cocobambu.delivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Tag(name = "Pedidos")
    @Operation(summary = "Listar todos os pedidos", description = "Retorna a lista completa de pedidos cadastrados no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<OrderWrapper>> listAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @Tag(name = "Pedidos")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna os detalhes de um pedido especifico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderWrapper> getById(
            @Parameter(description = "ID do pedido (UUID)", example = "a1b2c3d4-e5f6-4788-a999-b1c2d3e4f501")
            @PathVariable String id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @Tag(name = "Pedidos")
    @Operation(summary = "Criar novo pedido",
            description = "Cria um novo pedido no sistema. O status inicial sera automaticamente definido como RECEIVED. "
                    + "O order_id e created_at sao gerados automaticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do pedido invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<OrderWrapper> create(@RequestBody OrderWrapper orderWrapper) {
        OrderWrapper created = orderService.create(orderWrapper);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Tag(name = "Pedidos")
    @Operation(summary = "Atualizar pedido", description = "Atualiza os dados de um pedido existente. O order_id original e preservado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderWrapper> update(
            @Parameter(description = "ID do pedido (UUID)") @PathVariable String id,
            @RequestBody OrderWrapper orderWrapper) {
        return ResponseEntity.ok(orderService.update(id, orderWrapper));
    }

    @Tag(name = "Pedidos")
    @Operation(summary = "Excluir pedido", description = "Remove um pedido do sistema permanentemente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do pedido (UUID)") @PathVariable String id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Tag(name = "Status")
    @Operation(summary = "Atualizar status do pedido",
            description = "Atualiza o status de um pedido seguindo a maquina de estados. "
                    + "Transicoes validas: RECEIVED -> CONFIRMED -> DISPATCHED -> DELIVERED. "
                    + "CANCELED pode ser alcancado a partir de RECEIVED, CONFIRMED ou DISPATCHED. "
                    + "DELIVERED e CANCELED sao estados finais.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transicao de status invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderWrapper> updateStatus(
            @Parameter(description = "ID do pedido (UUID)") @PathVariable String id,
            @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }
}
