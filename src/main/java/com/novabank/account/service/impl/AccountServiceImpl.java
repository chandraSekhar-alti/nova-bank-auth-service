package com.novabank.account.service.impl;

import com.novabank.account.dto.request.CreateBankAccountRequestDto;
import com.novabank.account.dto.request.DepositRequestDto;
import com.novabank.account.dto.request.TransferRequestDto;
import com.novabank.account.dto.request.WithdrawRequestDto;
import com.novabank.account.dto.response.BankAccountResponseDto;
import com.novabank.account.entity.BankAccount;
import com.novabank.account.enums.AccountStatus;
import com.novabank.account.enums.TransactionType;
import com.novabank.account.mapper.BankAccountMapper;
import com.novabank.account.repository.BankAccountRepository;
import com.novabank.account.service.AccountService.AccountService;
import com.novabank.account.service.validation.AccountValidationServiceImpl;
import com.novabank.account.utils.AccountNumberGenerator;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import com.novabank.common.exceptions.ResourceNotFoundException;
import com.novabank.transaction.service.TramsactionService.TransactionAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionAuditService transactionAuditService;
    private final AccountValidationServiceImpl accountValidationService;
    private final AccountNumberGenerator accountNumberGenerator;
    private final BankAccountMapper bankAccountMapper;

    @Override
    public ApiResponseDto<BankAccountResponseDto> createBankAccount(
            CreateBankAccountRequestDto requestDto,
            String userEmail
    ) {
        log.info("Creating bank account for user: {}", userEmail);

        User user = getUserByEmail(userEmail);
        String accountNumber = accountNumberGenerator.generateAccountNumber();

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountType(requestDto.getAccountType());
        bankAccount.setAccountStatus(AccountStatus.ACTIVE);
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setUser(user);

        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        log.info("Bank account created for user {} with account number: {}", userEmail, savedAccount.getAccountNumber());

        return new ApiResponseDto<>(
                true,
                "Bank Account created successfully",
                bankAccountMapper.toResponseDto(savedAccount)
        );
    }

    @Override
    public ApiResponseDto<List<BankAccountResponseDto>> fetchALlBankAccounts(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        List<BankAccount> accounts =
                bankAccountRepository
                        .findByUser(user);

        List<BankAccountResponseDto> responseList =
                accounts.stream()
                        .map(
                                bankAccountMapper::toResponseDto
                        ).toList();

        return new ApiResponseDto<>(
                true,
                "Bank Accounts fetched successfully",
                responseList
        );

    }

    @Override
    @Transactional
    public ApiResponseDto<BankAccountResponseDto> depositMoney(
            DepositRequestDto requestDto,
            String userEmail
    ) {
        log.info("Deposit initiated. User: {}, Account Number: {}, Amount: {}", userEmail, requestDto.getAccountNumber(), requestDto.getAmount());

        User user = getUserByEmail(userEmail);
        BankAccount bankAccount = getAccountByNumber(requestDto.getAccountNumber());
        accountValidationService.validateAccountOwnership(bankAccount, user);
        accountValidationService.validateAccountStatusIsActive(bankAccount);

        BigDecimal newBalance =
                bankAccount.getBalance()
                        .add(requestDto.getAmount());

        bankAccount.setBalance(newBalance);

        BankAccount updatedAccount =
                bankAccountRepository
                        .save(bankAccount);

        transactionAuditService.createTransaction(
                updatedAccount,
                TransactionType.DEPOSIT,
                requestDto.getAmount(),
                "Amount deposited"
        );

        log.info("Deposit successful. User: {}, Account Number: {}, New Balance: {}", userEmail, requestDto.getAccountNumber(), updatedAccount.getBalance());

        return new ApiResponseDto<>(
                true,
                "Amount deposited successfully",
                bankAccountMapper.toResponseDto(updatedAccount)
        );

    }

    @Override
    @Transactional
    public ApiResponseDto<BankAccountResponseDto> withdrawMoney(
            WithdrawRequestDto requestDto,
            String userEmail
    ) {
        log.info("Withdrawal initiated. User: {}, Account Number: {}, Amount: {}", userEmail, requestDto.getAccountNumber(), requestDto.getAmount());
        User user = getUserByEmail(userEmail);

        BankAccount account = getAccountByNumber(requestDto.getAccountNumber());
        accountValidationService.validateAccountOwnership(account, user);
        accountValidationService.validateAccountStatusIsActive(account);
        accountValidationService.validateSufficientBalance(account, requestDto.getAmount());

        account.setBalance(
                account.getBalance()
                        .subtract(requestDto.getAmount())
        );

        BankAccount updatedAccount =
                bankAccountRepository
                        .save(account);

        transactionAuditService.createTransaction(
                updatedAccount,
                TransactionType.WITHDRAW,
                requestDto.getAmount(),
                "Amount withdrawn"
        );

        log.info("Withdrawal successful. User: {}, Account Number: {}, New Balance: {}", userEmail, requestDto.getAccountNumber(), updatedAccount.getBalance());

        return new ApiResponseDto<>(
                true,
                "Amount withdrawn successfully",
                bankAccountMapper.toResponseDto(updatedAccount)
        );

    }

    @Override
    @Transactional
    public ApiResponseDto<String>
    transferMoney(
            TransferRequestDto requestDto,
            String userEmail
    ) {

        log.info("Transfer initiated. User: {}, From Account: {}, To Account: {}, Amount: {}", userEmail, requestDto.getFromAccountNumber(), requestDto.getToAccountNumber(), requestDto.getAmount());

        if (requestDto.getFromAccountNumber().equals(requestDto.getToAccountNumber())) {
            log.warn("Transfer request with identical source and destination account: accountNumber={}", requestDto.getFromAccountNumber());
            throw new RuntimeException("Cannot transfer to the same account");
        }

        User user = getUserByEmail(userEmail);

        BankAccount senderAccount = getAccountByNumber(requestDto.getFromAccountNumber());
        BankAccount receiverAccount = getAccountByNumber(requestDto.getToAccountNumber());

        accountValidationService.validateAccountStatusIsActive(senderAccount);
        accountValidationService.validateAccountStatusIsActive(receiverAccount);

        accountValidationService.validateSufficientBalance(senderAccount, requestDto.getAmount());

        senderAccount.setBalance(
                senderAccount.getBalance()
                        .subtract(requestDto.getAmount())
        );

        receiverAccount.setBalance(
                receiverAccount.getBalance()
                        .add(requestDto.getAmount())
        );
        bankAccountRepository.save(senderAccount);
        bankAccountRepository.save(receiverAccount);

        transactionAuditService.createTransaction(
                senderAccount,
                TransactionType.TRANSACTION_OUT,
                requestDto.getAmount(),
                "Transfer to " + receiverAccount.getAccountNumber()
        );

        transactionAuditService.createTransaction(
                receiverAccount,
                TransactionType.TRANSACTION_IN,
                requestDto.getAmount(),
                "Transfer from " + senderAccount.getAccountNumber()
        );

        log.info("Transfer successful. userId={}, userEmail={}, From Account={}, To Account={}, Amount={}", user.getId(), userEmail, requestDto.getFromAccountNumber(), requestDto.getToAccountNumber(), requestDto.getAmount());

        return new ApiResponseDto<>(
                true,
                "Amount transferred successfully",
                null
        );

    }


    private User getUserByEmail(String userEmail) {

        return userRepository.findByEmail(userEmail)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found with email: " + userEmail
                        )
                );
    }

    private BankAccount getAccountByNumber(String accountNumber) {
        return bankAccountRepository.
                findByAccountNumber(accountNumber)
                .orElseThrow(
                        () -> {
                            log.warn("Bank account not found. accountNumber={}", accountNumber);
                            return new RuntimeException("Bank account not found with account number: " + accountNumber);
                        }
                );
    }

}
