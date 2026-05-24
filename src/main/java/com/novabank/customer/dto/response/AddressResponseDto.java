package com.novabank.customer.dto.response;

import com.novabank.customer.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponseDto {

    private Long id;

    private String addressLine1;

    private String addressLine2;

    private String state;

    private String city;

    private String country;

    private String pinCode;

    private AddressType addressType;
}
