package com.cocobambu.delivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @JsonProperty("temporary_phone")
    private String temporaryPhone;

    private String name;
}
