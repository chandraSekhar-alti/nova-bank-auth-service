package com.novabank.auth.service.AuthService;

import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.ApiResponseDto;

public interface AuthService {

    ApiResponseDto<String> registerUser(RegisterRequestDto registerRequestDto);
}
