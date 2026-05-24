package com.novabank.customer.controller;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.customer.dto.request.CompleteCustomerProfileRequestDto;
import com.novabank.customer.dto.response.CustomerProfileResponseDto;
import com.novabank.customer.service.impl.CustomerProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer-profile")
public class CustomerProfileController {

    private final CustomerProfileServiceImpl customerProfileService;

    @PostMapping
    public ResponseEntity<
            ApiResponseDto<CustomerProfileResponseDto>
            > completeCustomerProfile(
            @Valid
            @RequestBody
            CompleteCustomerProfileRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail =authentication.getName();

        ApiResponseDto<CustomerProfileResponseDto>
                response = customerProfileService.completeProfile(
                        requestDto,
                userEmail
        );

        return ResponseEntity.ok(response);
    }

}
