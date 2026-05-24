package com.novabank.customer.controller;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.customer.dto.request.AddAddressRequestDto;
import com.novabank.customer.dto.response.AddressResponseDto;
import com.novabank.customer.entity.Address;
import com.novabank.customer.service.customerService.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<AddressResponseDto>> addAddress(
            @Valid
            @RequestBody
            AddAddressRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        ApiResponseDto<AddressResponseDto> response =
                addressService.addAddress(
                        requestDto,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }
}
