package com.cocobambu.delivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Integer code;
    private Double price;
    private String observations;

    @JsonProperty("total_price")
    private Double totalPrice;

    private String name;
    private Integer quantity;
    private Double discount;
    private List<Object> condiments;
}
