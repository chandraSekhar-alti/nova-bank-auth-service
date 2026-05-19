package com.novabank.transaction.service.impl;

import com.novabank.account.entity.BankAccount;
import com.novabank.account.repository.BankAccountRepository;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import com.novabank.transaction.dto.response.TransactionResponseDto;
import com.novabank.transaction.mapper.TransactionMapper;
import com.novabank.transaction.repository.TransactionRepository;
import com.novabank.transaction.service.TramsactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponseDto<List<TransactionResponseDto>>
    getTransactions(
            String accountNumber,
            String userEmail
    ) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        BankAccount bankAccount =
                bankAccountRepository
                        .findByAccountNumber(
                                accountNumber
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Account not found"
                                )
                        );

        if (!bankAccount.getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "Unauthorized account access"
            );
        }

        List<TransactionResponseDto>
                responseList =
                transactionRepository
                        .findByBankAccountOrderByCreatedAtDesc(
                                bankAccount
                        )
                        .stream()
                        .map(
                                TransactionMapper
                                        ::toTransactionResponse
                        )
                        .toList();

        return new ApiResponseDto<>(
                true,
                "Transactions fetched successfully",
                responseList
        );
    }
}
