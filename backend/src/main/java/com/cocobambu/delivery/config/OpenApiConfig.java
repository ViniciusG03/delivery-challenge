package com.cocobambu.delivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery API")
                        .description("API REST para gerenciamento de pedidos de delivery. "
                                + "Permite criar, consultar, atualizar e excluir pedidos, "
                                + "alem de controlar a maquina de estados do ciclo de vida do pedido.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Coco Bambu")))
                .tags(List.of(
                        new Tag().name("Pedidos").description("Operacoes CRUD de pedidos"),
                        new Tag().name("Status").description("Gerenciamento da maquina de estados do pedido")
                ));
    }
}
