package com.cocobambu.delivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {

    private String reference;

    @JsonProperty("street_name")
    private String streetName;

    @JsonProperty("postal_code")
    private String postalCode;

    private String country;
    private String city;
    private String neighborhood;

    @JsonProperty("street_number")
    private String streetNumber;

    private String state;
    private Coordinates coordinates;
}
