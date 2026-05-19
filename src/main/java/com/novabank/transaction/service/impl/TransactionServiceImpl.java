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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Service class for transaction-related operations.
 * Handles retrieval of bank account transactions with proper authorization.
 *
 * Enterprise Standards Applied:
 * - Input validation to prevent null/empty values
 * - Specific domain exceptions for different failure scenarios
 * - Security-sensitive operations logged for audit trails
 * - Read-only transactional operations marked appropriately
 * - Proper authorization checks to prevent unauthorized data access
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    // Dependencies injected via constructor
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all transactions for a specific bank account.
     *
     * WHY @Transactional(readOnly = true):
     * - Optimizes database performance for read-only operations
     * - Prevents accidental modifications within the method
     * - Enables database query optimizations for readonly transactions
     * - Proper resource management with implicit session closure
     *
     * @param accountNumber The account number to fetch transactions for
     * @param userEmail     The email of the authenticated user
     * @return ApiResponseDto containing list of transactions
     * @throws ResourceNotFoundException if user is not found
     * @throws AccountNotFoundException  if account is not found
     * @throws InvalidAccountAccessException if user lacks permission to access account
     */
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDto<List<TransactionResponseDto>> getTransactions(
            String accountNumber,
            String userEmail
    ) {
        // ==================== INPUT VALIDATION ====================
        // WHY: Prevents null pointer exceptions and ensures API contract compliance
        validateInputParameters(accountNumber, userEmail);

        // ==================== USER AUTHENTICATION ====================
        // WHY: Verify user exists before accessing their account.
        //      Fail-fast principle - prevent unnecessary database queries.
        //      Custom exception for clear error messaging.
        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.warn("User authentication failed: Email not found - {}", userEmail);
                    return new ResourceNotFoundException(
                            "User not found with email: " + userEmail
                    );
                });

        log.debug("User authenticated: userId={}", user.getId());

        // ==================== ACCOUNT LOOKUP ====================
        // WHY: Specific AccountNotFoundException distinguishes account lookup failures
        //      from general resource not found errors, improving debugging and monitoring.
        BankAccount bankAccount = bankAccountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.warn(
                            "Account lookup failed: accountNumber={}, requestedBy={}",
                            accountNumber,
                            userEmail
                    );
                    return new AccountNotFoundException(
                            "Bank account not found with number: " + accountNumber
                    );
                });

        log.debug("Account found: accountId={}, accountNumber={}", bankAccount.getId(), accountNumber);

        // ==================== AUTHORIZATION CHECK ====================
        // WHY: Critical security operation - verify user owns the account before returning sensitive data.
        //      InvalidAccountAccessException (403 Forbidden) is distinct from UnauthorizedException (401).
        //      Logs unauthorized attempts for fraud detection.
        if (!isAccountOwner(bankAccount, user)) {
            log.error(
                    "SECURITY ALERT: Unauthorized account access attempt - userId={}, accountNumber={}, accountOwnerId={}",
                    user.getId(),
                    accountNumber,
                    bankAccount.getUser().getId()
            );
            throw new InvalidAccountAccessException(
                    "Access denied: You do not have permission to view transactions for account " + accountNumber
            );
        }

        log.info("Authorization successful: User accessing their own account transactions - userId={}, accountId={}",
                user.getId(), bankAccount.getId());

        // ==================== FETCH TRANSACTIONS ====================
        // WHY: Eager fetch and convert to list to ensure all operations complete within
        //      the transaction context. Prevents lazy loading issues after session closes.
        List<TransactionResponseDto> responseList = transactionRepository
                .findByBankAccountOrderByCreatedAtDesc(bankAccount)
                .stream()
                .map(TransactionMapper::toTransactionResponse)
                .toList();

        log.info("Transactions retrieved successfully - userId={}, accountId={}, transactionCount={}",
                user.getId(), bankAccount.getId(), responseList.size());

        return new ApiResponseDto<>(
                true,
                "Transactions fetched successfully",
                responseList
        );
    }

    /**
     * Validates input parameters to ensure non-null and non-empty values.
     *
     * WHY: Prevents NullPointerException and ensures API contract compliance.
     *      Business logic depends on valid inputs.
     *
     * @param accountNumber The account number to validate
     * @param userEmail     The user email to validate
     * @throws IllegalArgumentException if any parameter is invalid
     */
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

    /**
     * Checks if a user owns a specific bank account.
     *
     * WHY: Encapsulates authorization logic for reusability and maintainability.
     *      Single responsibility principle - separate authorization from business logic.
     *
     * @param bankAccount The bank account to check ownership of
     * @param user        The user to verify ownership for
     * @return true if user owns the account, false otherwise
     */
    private boolean isAccountOwner(BankAccount bankAccount, User user) {
        return bankAccount.getUser() != null &&
                bankAccount.getUser().getId() != null &&
                bankAccount.getUser().getId().equals(user.getId());
    }
}
