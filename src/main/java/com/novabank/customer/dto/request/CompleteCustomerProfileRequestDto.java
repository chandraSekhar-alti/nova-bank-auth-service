package com.novabank.customer.dto.request;

import com.novabank.customer.enums.EmploymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
public class CompleteCustomerProfileRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Marital status is required")
    private String maritalStatus;

    @NotBlank(message = "Father or spouse name is required")
    private String fatherOrSpouseName;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Annual income is required")
    @DecimalMin(value = "0.00", message = "Annual income must be greater than zero")
    private BigDecimal annualIncome;

}
