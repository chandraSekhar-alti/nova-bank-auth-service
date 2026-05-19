package com.novabank.transaction.service.TramsactionService;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.transaction.dto.response.TransactionResponseDto;

import java.util.List;

public interface TransactionService {

    ApiResponseDto<List<TransactionResponseDto>>
    getTransactions(
            String accountNumber,
            String userEmail
    );
}
