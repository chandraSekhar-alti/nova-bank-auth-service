package com.novabank.customer.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProfileResponseDto {

    private String firstName;
    private String lastName;
    private String gender;
    private String maritalStatus;
    private String fatherOrSpouseName;
    private String employmentType;
    private String annualIncome;
    private String dateOfBirth;
}
