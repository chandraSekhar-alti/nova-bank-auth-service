package com.novabank.customer.service.customerService;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.customer.dto.request.AddKycRequestDto;
import com.novabank.customer.dto.response.KycDetailsResponseDto;

public interface KycService {

    ApiResponseDto<KycDetailsResponseDto>
    addKycDetails(
            AddKycRequestDto requestDto,
            String userEmail
    );
}
