package com.novabank.customer.dto.response;

import com.novabank.customer.enums.KycStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycDetailsResponseDto {

    private String panNumber;

    private String maskedAadhaarNumber;

    private KycStatus kycStatus;

    private boolean panVerified;

    private boolean aadhaarVerified;
}
