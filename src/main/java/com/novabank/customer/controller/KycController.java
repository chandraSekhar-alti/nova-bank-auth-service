package com.novabank.customer.controller;


import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.customer.dto.request.AddKycRequestDto;
import com.novabank.customer.dto.response.KycDetailsResponseDto;
import com.novabank.customer.service.impl.KycServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycServiceImpl kycService;

    @PostMapping
    public ResponseEntity<
            ApiResponseDto<KycDetailsResponseDto>
            > addKycDetails(
            @Valid
            @RequestBody
            AddKycRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        ApiResponseDto<KycDetailsResponseDto>
                response =
                kycService.addKycDetails(
                        requestDto,
                        userEmail
                );

        return ResponseEntity.ok(response);

    }
}
