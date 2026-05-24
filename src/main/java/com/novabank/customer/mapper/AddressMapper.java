package com.novabank.customer.mapper;

import com.novabank.customer.dto.response.AddressResponseDto;
import com.novabank.customer.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponseDto toResponseDto(Address address){
        AddressResponseDto response = new AddressResponseDto();

        response.setId(address.getId());
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPinCode(address.getPinCode());
        response.setAddressType(address.getAddressType());

        return response;
    }
}
