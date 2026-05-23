package com.novabank.transaction.controller;


import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.transaction.dto.response.TransactionResponseDto;
import com.novabank.transaction.service.TramsactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<
                ApiResponseDto<Page<TransactionResponseDto>>
                > getTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {

        String userEmail =
                authentication.getName();

        ApiResponseDto<Page<TransactionResponseDto>>
                response =
                transactionService.getTransactions(
                        accountNumber,
                        userEmail,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }
}
