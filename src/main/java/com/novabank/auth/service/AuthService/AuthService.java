package com.novabank.auth.service.AuthService;

import com.novabank.auth.dto.request.*;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.dto.response.ForgotPasswordResponseDto;
import com.novabank.auth.dto.response.LoginResponseDto;
import com.novabank.auth.dto.response.RefreshTokenResponseDto;

public interface AuthService {

    ApiResponseDto<String> registerUser(RegisterRequestDto registerRequestDto);

    ApiResponseDto<LoginResponseDto> loginUser(LoginRequestDto requestDto);

    ApiResponseDto<RefreshTokenResponseDto> refreshAccessToken(RefreshTokenRequestDto requestDto);

    ApiResponseDto<String> logoutUser (LogoutRequestDto logoutRequestDto);

    ApiResponseDto<ForgotPasswordResponseDto> forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);

    ApiResponseDto<String> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
}
