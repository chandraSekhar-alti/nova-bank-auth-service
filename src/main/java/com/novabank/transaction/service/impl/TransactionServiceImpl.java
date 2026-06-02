package com.novabank.transaction.service.impl;

import com.novabank.account.entity.BankAccount;
import com.novabank.account.repository.BankAccountRepository;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import com.novabank.common.exceptions.AccountNotFoundException;
import com.novabank.common.exceptions.InvalidAccountAccessException;
import com.novabank.common.exceptions.ResourceNotFoundException;
import com.novabank.transaction.dto.response.TransactionResponseDto;
import com.novabank.transaction.mapper.TransactionMapper;
import com.novabank.transaction.repository.TransactionRepository;
import com.novabank.transaction.service.TramsactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    // Dependencies injected via constructor
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDto<Page<TransactionResponseDto>> getTransactions(
            String accountNumber,
            String userEmail,
            int page,
            int size
    ) {

        validateInputParameters(accountNumber, userEmail);

        User user = getUserByEmail(userEmail);
        log.debug("User authenticated: userId={}", user.getId());

        BankAccount bankAccount = findBankAccountByAccountNumber(accountNumber, userEmail);
        log.debug("Account found: accountId={}, accountNumber={}", bankAccount.getId(), accountNumber);

        validateAccountOwnership(bankAccount, user, accountNumber);
        log.info("Authorization successful: User accessing their own account transactions - userId={}, accountId={}", user.getId(), bankAccount.getId());

        Pageable pageable = PageRequest.of(page, size);

        Page<TransactionResponseDto> responseList =
                transactionRepository
                        .findByBankAccountOrderByCreatedAtDesc(
                                bankAccount,
                                pageable
                        )
                        .map(transactionMapper::toTransactionResponse);

        log.info("Transactions retrieved successfully - userId={}, accountId={}, transactionCount={}", user.getId(), bankAccount.getId(), responseList);

        return new ApiResponseDto<>(
                true,
                "Transactions fetched successfully",
                responseList
        );
    }

    private void validateInputParameters(String accountNumber, String userEmail) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            log.warn("Input validation failed: accountNumber is null or empty");
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            log.warn("Input validation failed: userEmail is null or empty");
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
    }

    private boolean isAccountOwner(BankAccount bankAccount, User user) {
        return bankAccount.getUser() != null &&
                bankAccount.getUser().getId() != null &&
                bankAccount.getUser().getId().equals(user.getId());
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found with email: " + userEmail
                        )
                );
    }

    private BankAccount findBankAccountByAccountNumber(String accountNumber, String userEmail) {
        return bankAccountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.warn("Account lookup failed: accountNumber={}, requestedBy={}", accountNumber, userEmail);
                    return new AccountNotFoundException(
                            "Bank account not found with number: " + accountNumber
                    );
                });
    }

    private void validateAccountOwnership(BankAccount bankAccount, User user, String accountNumber){
        if(!isAccountOwner(bankAccount, user)){
            log.error("SECURITY ALERT: Unauthorized account access attempt - userId={}, accountNumber={}, accountOwnerId={}", user.getId(), accountNumber, bankAccount.getUser().getId());

            throw new InvalidAccountAccessException(
                    "Access denied: You do not have permission to view transactions for account " + accountNumber
            );
        }
    }

}
