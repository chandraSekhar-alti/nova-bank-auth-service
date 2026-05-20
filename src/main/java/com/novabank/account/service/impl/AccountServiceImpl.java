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
import com.novabank.common.exceptions.InsufficientBalanceException;
import com.novabank.common.exceptions.ResourceNotFoundException;
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

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found with email: " + userEmail
                        )
                );

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
        User user = userRepository.
                findByEmail(userEmail)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "user not found"
                        )
                );

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

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"
                        )
                );

        BankAccount bankAccount =
                bankAccountRepository
                        .findByAccountNumber(
                                requestDto.getAccountNumber()
                        )
                        .orElseThrow(() -> {
                            log.warn("Bank account not found for deposit. accountNumber={}", requestDto.getAccountNumber());
                            return new RuntimeException("Bank account not found with account number: " + requestDto.getAccountNumber());
                        });

        if (!bankAccount.getUser().getId().equals(user.getId())) {
            log.error("Unauthorized access attempt for deposit. userEmail={}, accountNumber={}", user.getEmail(), requestDto.getAccountNumber());
            throw new RuntimeException("Unauthorized access to bank account");
        }

        if (bankAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("Attempt to deposit to inactive account: accountNumber={}", requestDto.getAccountNumber());
            throw new RuntimeException("Cannot deposit to an inactive account");
        }

        BigDecimal newBalance =
                bankAccount.getBalance()
                        .add(requestDto.getAmount());

        bankAccount.setBalance(newBalance);

        BankAccount updatedAccount =
                bankAccountRepository
                        .save(bankAccount);

        Transaction transaction = new Transaction();

        transaction.setTransactionType(
                TransactionType.DEPOSIT
        );

        transaction.setAmount(
                requestDto.getAmount()
        );

        transaction.setDescription(
                "Amount deposited"
        );

        transaction.setBankAccount(updatedAccount);

        transactionRepository.save(transaction);

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
        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        BankAccount account =
                bankAccountRepository
                        .findByAccountNumber(
                                requestDto.getAccountNumber()
                        )
                        .orElseThrow(() -> {
                            log.warn("Bank account not found for withdrawal. accountNumber={}", requestDto.getAccountNumber());
                            return new RuntimeException("Bank account not found with account number: " + requestDto.getAccountNumber());
                        });

        if (!account.getUser().getId().equals(user.getId())) {
            log.error("Unauthorized access attempt for withdrawal. userEmail={}, accountNumber={}", user.getEmail(), requestDto.getAccountNumber());
            throw new RuntimeException("Unauthorized access to bank account");
        }

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("Attempt to withdraw from inactive account: accountNumber={}", requestDto.getAccountNumber());
            throw new RuntimeException("Cannot withdraw from an inactive account");
        }

        if (account.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        account.setBalance(
                account.getBalance()
                        .subtract(requestDto.getAmount())
        );

        BankAccount updatedAccount =
                bankAccountRepository
                        .save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAmount(requestDto.getAmount());
        transaction.setDescription("Amount withdrawn");
        transaction.setBankAccount(updatedAccount);

        transactionRepository.save(transaction);

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

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        BankAccount senderAccount =
                bankAccountRepository
                        .findByAccountNumber(
                                requestDto.getFromAccountNumber()
                        )
                        .orElseThrow(() -> {
                            log.warn("Sender account not found for transfer: {}", requestDto.getFromAccountNumber());
                            return new RuntimeException("Sender account not found with account number: " + requestDto.getFromAccountNumber());
                        });

        BankAccount receiverAccount =
                bankAccountRepository
                        .findByAccountNumber(
                                requestDto.getToAccountNumber()
                        )
                        .orElseThrow(() -> {
                            log.warn("Receiver account not found for transfer: {}", requestDto.getToAccountNumber());
                            return new RuntimeException("Receiver account not found with account number: " + requestDto.getToAccountNumber());
                        });

        if (senderAccount.getAccountStatus() != AccountStatus.ACTIVE || receiverAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("Transfer attempted between inactive accounts. from={}, to={}", requestDto.getFromAccountNumber(), requestDto.getToAccountNumber());
            throw new RuntimeException("Both accounts must be active for transfer");
        }

        if (senderAccount.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in sender account"
            );
        }

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

        Transaction senderTransaction = new Transaction();
        senderTransaction.setTransactionType(TransactionType.TRANSACTION_OUT);
        senderTransaction.setAmount(requestDto.getAmount());
        senderTransaction.setDescription("Transfer to " + receiverAccount.getAccountNumber());
        senderTransaction.setBankAccount(senderAccount);
        transactionRepository.save(senderTransaction);


        Transaction receiverTransaction = new Transaction();

        receiverTransaction.setTransactionType(TransactionType.TRANSACTION_IN);
        receiverTransaction.setAmount(requestDto.getAmount());
        receiverTransaction.setDescription("Transfer from " + senderAccount.getAccountNumber());
        receiverTransaction.setBankAccount(receiverAccount);
        transactionRepository.save(receiverTransaction);

        log.info("Transfer successful. userId={}, userEmail={}, From Account={}, To Account={}, Amount={}", user.getId(), userEmail, requestDto.getFromAccountNumber(), requestDto.getToAccountNumber(), requestDto.getAmount());

        return new ApiResponseDto<>(
                true,
                "Amount transfred successfully",
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
}
