package com.novabank.customer.service.customerService;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.customer.dto.request.CompleteCustomerProfileRequestDto;
import com.novabank.customer.dto.response.CustomerProfileResponseDto;

public interface CustomerProfileService {

    ApiResponseDto<CustomerProfileResponseDto>
    completeProfile(
            CompleteCustomerProfileRequestDto requestDto,
            String userEmail
    );
}
