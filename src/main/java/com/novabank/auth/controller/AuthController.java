package com.novabank.auth.controller;

import com.novabank.auth.dto.request.LoginRequestDto;
import com.novabank.auth.dto.request.LogoutRequestDto;
import com.novabank.auth.dto.request.RefreshTokenRequestDto;
import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.dto.response.LoginResponseDto;
import com.novabank.auth.dto.response.RefreshTokenResponseDto;
import com.novabank.auth.service.AuthService.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<String>>
    registerUser(
            @Valid
            @RequestBody
            RegisterRequestDto requestDto
    ) {

        ApiResponseDto<String> response =
                authService.registerUser(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<
            ApiResponseDto<LoginResponseDto>
            > loginUser(
            @Valid
            @RequestBody
            LoginRequestDto requestDto
    ) {

        ApiResponseDto<LoginResponseDto> response =
                authService.loginUser(requestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<
            ApiResponseDto<RefreshTokenResponseDto>
            > refreshToken(
            @Valid
            @RequestBody
            RefreshTokenRequestDto requestDto
    ) {

        ApiResponseDto<RefreshTokenResponseDto>
                response =
                authService.refreshAccessToken(
                        requestDto
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<String>> logoutUser(
            @Valid
            @RequestBody
            LogoutRequestDto logoutRequestDto
    ){
        ApiResponseDto<String> response = authService.logoutUser(logoutRequestDto);
        return ResponseEntity.ok(response);
    }
}
