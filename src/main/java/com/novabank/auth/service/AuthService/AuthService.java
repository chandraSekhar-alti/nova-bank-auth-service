package com.novabank.auth.service.AuthService;

import com.novabank.auth.dto.request.LoginRequestDto;
import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.dto.response.LoginResponseDto;

public interface AuthService {

    ApiResponseDto<String> registerUser(RegisterRequestDto registerRequestDto);

    ApiResponseDto<LoginResponseDto> loginUser(LoginRequestDto requestDto);
}
