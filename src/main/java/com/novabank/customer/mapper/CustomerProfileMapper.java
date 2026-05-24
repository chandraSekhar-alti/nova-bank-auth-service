package com.novabank.customer.mapper;

import com.novabank.customer.dto.response.CustomerProfileResponseDto;
import com.novabank.customer.entity.CustomerProfile;
import org.springframework.stereotype.Component;

@Component
public class CustomerProfileMapper {

    public CustomerProfileResponseDto toCustomerProfileResponseDto(
            CustomerProfile customerProfile
    ){
        CustomerProfileResponseDto response = new CustomerProfileResponseDto();

        response.setFirstName(customerProfile.getFirstName());
        response.setLastName(customerProfile.getLastName());
        response.setGender(customerProfile.getGender().name());
        response.setMaritalStatus(customerProfile.getMaritalStatus().name());
        response.setFatherOrSpouseName(customerProfile.getFatherOrSpouseName());
        response.setEmploymentType(customerProfile.getEmploymentType().name());
        response.setAnnualIncome(customerProfile.getAnnualIncome().toString());
        response.setDateOfBirth(customerProfile.getDateOfBirth().toString());

        return response;
    }

}
