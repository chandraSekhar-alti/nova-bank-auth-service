package com.novabank.auth.service.AuthService;

import com.novabank.auth.dto.request.LoginRequestDto;
import com.novabank.auth.dto.request.LogoutRequestDto;
import com.novabank.auth.dto.request.RefreshTokenRequestDto;
import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.dto.response.LoginResponseDto;
import com.novabank.auth.dto.response.RefreshTokenResponseDto;

public interface AuthService {

    ApiResponseDto<String> registerUser(RegisterRequestDto registerRequestDto);

    ApiResponseDto<LoginResponseDto> loginUser(LoginRequestDto requestDto);

    ApiResponseDto<RefreshTokenResponseDto> refreshAccessToken(RefreshTokenRequestDto requestDto);

    ApiResponseDto<String> logoutUser (LogoutRequestDto logoutRequestDto);
}
