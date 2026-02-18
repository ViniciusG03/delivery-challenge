package com.cocobambu.delivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderWrapper {

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("order_id")
    private String orderId;

    private Order order;
}
