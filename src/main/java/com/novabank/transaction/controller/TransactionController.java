package com.novabank.transaction.controller;


import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.transaction.dto.response.TransactionResponseDto;
import com.novabank.transaction.service.TramsactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<
                ApiResponseDto<List<TransactionResponseDto>>
                > getTransactions(
            @PathVariable String accountNumber,
            Authentication authentication
    ) {

        String userEmail =
                authentication.getName();

        ApiResponseDto<List<TransactionResponseDto>>
                response =
                transactionService.getTransactions(
                        accountNumber,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }
}
