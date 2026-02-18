package com.cocobambu.delivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private List<Payment> payments;

    @JsonProperty("last_status_name")
    private String lastStatusName;

    private Store store;

    @JsonProperty("total_price")
    private Double totalPrice;

    @JsonProperty("order_id")
    private String orderId;

    private List<Item> items;

    @JsonProperty("created_at")
    private Long createdAt;

    private List<OrderStatus> statuses;

    private Customer customer;

    @JsonProperty("delivery_address")
    private DeliveryAddress deliveryAddress;
}
