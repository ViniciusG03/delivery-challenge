package com.cocobambu.delivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus {

    @JsonProperty("created_at")
    private Long createdAt;

    private String name;

    @JsonProperty("order_id")
    private String orderId;

    private String origin;
}
