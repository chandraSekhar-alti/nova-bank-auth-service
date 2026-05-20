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
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import com.novabank.common.exceptions.BadRequestException;
import com.novabank.common.exceptions.InsufficientBalanceException;
import com.novabank.common.exceptions.ResourceNotFoundException;
import com.novabank.common.exceptions.UnauthorizedException;
import com.novabank.transaction.entity.Transaction;
import com.novabank.transaction.repository.TransactionRepository;
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
    private final TransactionRepository transactionRepository;

    @Override
    public ApiResponseDto<BankAccountResponseDto> createBankAccount(
            CreateBankAccountRequestDto requestDto,
            String userEmail
    ) {
        log.info("Creating bank account for user: {}", userEmail);

        User user = getUserByEmail(userEmail);
        String accountNumber = generateAccountNumber();

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
                BankAccountMapper.toResponseDto(savedAccount)
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
                                BankAccountMapper::toResponseDto
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
        validateAccountOwnership(bankAccount, user);
        validateAccountStatusIsActive(bankAccount);

        BigDecimal newBalance =
                bankAccount.getBalance()
                        .add(requestDto.getAmount());

        bankAccount.setBalance(newBalance);

        BankAccount updatedAccount =
                bankAccountRepository
                        .save(bankAccount);

        createTransaction(
                updatedAccount,
                TransactionType.DEPOSIT,
                requestDto.getAmount(),
                "Amount deposited"
        );

        log.info("Deposit successful. User: {}, Account Number: {}, New Balance: {}", userEmail, requestDto.getAccountNumber(), updatedAccount.getBalance());

        return new ApiResponseDto<>(
                true,
                "Amount deposited successfully",
                BankAccountMapper.toResponseDto(updatedAccount)
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
        validateAccountOwnership(account, user);
        validateAccountStatusIsActive(account);
        validateSufficientBalance(account, requestDto.getAmount());

        account.setBalance(
                account.getBalance()
                        .subtract(requestDto.getAmount())
        );

        BankAccount updatedAccount =
                bankAccountRepository
                        .save(account);

        createTransaction(
                updatedAccount,
                TransactionType.WITHDRAW,
                requestDto.getAmount(),
                "Amount withdrawn"
        );

        log.info("Withdrawal successful. User: {}, Account Number: {}, New Balance: {}", userEmail, requestDto.getAccountNumber(), updatedAccount.getBalance());

        return new ApiResponseDto<>(
                true,
                "Amount withdrawn successfully",
                BankAccountMapper.toResponseDto(updatedAccount)
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

        validateAccountStatusIsActive(senderAccount);
        validateAccountStatusIsActive(receiverAccount);

        validateSufficientBalance(senderAccount, requestDto.getAmount());

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

        createTransaction(
                senderAccount,
                TransactionType.TRANSACTION_OUT,
                requestDto.getAmount(),
                "Transfer to " + receiverAccount.getAccountNumber()
        );

        createTransaction(
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


    private String generateAccountNumber() {
        /*
         * Need to implement later :
         * Branch/Region Code (2–3 digits): Identifies where the account was opened.
         * Product/Account Type Code (2 digits): Identifies if it's Savings (e.g., 10), Current (20), Loan (30), etc.
         * Unique Sequence Number (5–7 digits): A sequential or masked auto-incrementing number from your database.
         * Check Digit (1 digit): A final digit calculated using a checksum algorithm (like Luhn or Modulo 11) to catch typos and data entry errors.
         */

        Random random = new Random();
        return "NB"
                + (100000000)
                + random.nextInt(900000000);
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

    private void validateAccountOwnership(BankAccount bankAccount, User user) {
        if (!bankAccount.getId().equals(user.getId())) {
            log.warn("Unauthorized access attempt. userEmail={}, accountNumber={}", user.getEmail(), bankAccount.getAccountNumber());
            throw new UnauthorizedException("Unauthorized access to bank account");
        }
    }

    private void validateAccountStatusIsActive(BankAccount bankAccount) {
        if (bankAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("Attempt to operate on inactive account: accountNumber={}", bankAccount.getAccountNumber());
            throw new BadRequestException("Bank account is not active");
        }
    }

    private void validateSufficientBalance(BankAccount bankAccount, BigDecimal amount) {
        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + bankAccount.getAccountNumber());
        }
    }

    private void createTransaction(BankAccount account, TransactionType transactionType, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();

        transaction.setBankAccount(account);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transactionRepository.save(transaction);
    }

}
