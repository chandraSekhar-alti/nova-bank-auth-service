package com.novabank.customer.service.customerService;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.customer.dto.request.AddAddressRequestDto;
import com.novabank.customer.dto.response.AddressResponseDto;

public interface AddressService {

    ApiResponseDto<AddressResponseDto>
    addAddress(
            AddAddressRequestDto requestDto,
            String userEmail
    );
}
