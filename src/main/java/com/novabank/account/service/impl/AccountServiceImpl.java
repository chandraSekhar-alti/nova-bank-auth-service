package com.novabank.account.service.impl;

import com.novabank.account.dto.request.CreateBankAccountRequestDto;
import com.novabank.account.dto.request.DepositRequestDto;
import com.novabank.account.dto.request.WithdrawRequestDto;
import com.novabank.account.dto.response.BankAccountResponseDto;
import com.novabank.account.entity.BankAccount;
import com.novabank.account.enums.AccountStatus;
import com.novabank.account.mapper.BankAccountMapper;
import com.novabank.account.repository.BankAccountRepository;
import com.novabank.account.service.AccountService.AccountService;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponseDto<BankAccountResponseDto> createBankAccount(
            CreateBankAccountRequestDto requestDto,
            String userEmail
    ) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        () -> new RuntimeException(
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
                        () -> new RuntimeException(
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
        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        BankAccount bankAccount =
                bankAccountRepository
                        .findByAccountNumber(
                                requestDto.getAccountNumber()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Bank account not found with account number: " + requestDto.getAccountNumber()
                                )
                        );

        if (!bankAccount.getUser()
                .getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Unauthorized access to bank account"
            );
        }

        if (bankAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException(
                    "Cannot deposit to an inactive account"
            );
        }

        BigDecimal newBalance =
                bankAccount.getBalance()
                        .add(requestDto.getAmount());

        bankAccount.setBalance(newBalance);

        BankAccount updatedAmount =
                bankAccountRepository
                        .save(bankAccount);

        return new ApiResponseDto<>(
                true,
                "Amount deposited successfully",
                BankAccountMapper.toResponseDto(updatedAmount)
        );

    }

    @Override
    public ApiResponseDto<BankAccountResponseDto> withdrawMoney(
            WithdrawRequestDto requestDto,
            String userEmail
    ) {
        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );

        BankAccount account =
                bankAccountRepository
                        .findByAccountNumber(
                                requestDto.getAccountNumber()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Bank account not found with account number: " + requestDto.getAccountNumber()
                                )
                        );

        if (!account.getUser()
                .getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Unauthorized access to bank account"
            );
        }

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException(
                    "Cannot withdraw from an inactive account"
            );
        }

        if (account.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new RuntimeException(
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

        return new ApiResponseDto<>(
                true,
                "Amount withdrawn successfully",
                BankAccountMapper.toResponseDto(updatedAccount)
        );

    }


    private String generateAccountNumber() {
        /**
         * Need to implement later :
         * Branch/Region Code (2–3 digits): Identifies where the account was opened.
         * Product/Account Type Code (2 digits): Identifies if it's Savings (e.g., 10), Current (20), Loan (30), etc.
         * Unique Sequence Number (5–7 digits): A sequential or masked auto-incrementing number from your database.
         * Check Digit (1 digit): A final digit calculated using a checksum algorithm (like Luhn or Modulo 11) to catch typos and data entry errors.
         * */

        Random random = new Random();
        return "NB"
                + (100000000)
                + random.nextInt(900000000);
    }
}
