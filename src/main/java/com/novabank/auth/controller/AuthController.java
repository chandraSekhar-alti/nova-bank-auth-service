package com.novabank.auth.controller;

import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.ApiResponseDto;
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
}
