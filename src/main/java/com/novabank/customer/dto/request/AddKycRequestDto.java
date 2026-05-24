package com.novabank.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddKycRequestDto {

    @NotBlank(message = "PAN number is required")
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "Invalid PAN format"
    )
    private String panNumber;

    @NotBlank(
            message = "Aadhaar number is required"
    )
    @Pattern(
            regexp = "^[0-9]{12}$",
            message = "Invalid Aadhaar format"
    )
    private String aadhaarNumber;
}
