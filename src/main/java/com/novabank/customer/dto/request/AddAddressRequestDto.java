package com.novabank.customer.dto.request;

import com.novabank.customer.enums.AddressType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAddressRequestDto {

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    @NotNull(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotNull(message = "City is required")
    private String city;

    @NotNull(message = "State is required")
    private String state;

    @NotNull(message = "Country is required")
    private String country;

    @NotNull(message = "Pin code is required")
    private String pinCode;


}
