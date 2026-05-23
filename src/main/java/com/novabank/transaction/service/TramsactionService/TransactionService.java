package com.novabank.transaction.service.TramsactionService;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.transaction.dto.response.TransactionResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {

    ApiResponseDto<Page<TransactionResponseDto>>
    getTransactions(
            String accountNumber,
            String userEmail,
            int page,
            int size
    );
}
