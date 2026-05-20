package com.novabank.account.service.validation;

import com.novabank.account.entity.BankAccount;
import com.novabank.account.enums.AccountStatus;
import com.novabank.auth.entity.User;
import com.novabank.common.exceptions.BadRequestException;
import com.novabank.common.exceptions.InsufficientBalanceException;
import com.novabank.common.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class AccountValidationServiceImpl implements AccountValidationService {
    @Override
    public void validateAccountOwnership(BankAccount bankAccount, User user) {
        if (!bankAccount.getId().equals(user.getId())) {
            log.warn("Unauthorized account access. User: {}, Account: {}", user.getEmail(), bankAccount.getAccountNumber());
            throw new UnauthorizedException("Unauthorized account access");
        }
    }

    @Override
    public void validateAccountStatusIsActive(BankAccount bankAccount) {
        if (bankAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("Account is not active. Account: {}, Status: {}", bankAccount.getAccountNumber(), bankAccount.getAccountStatus());
            throw new BadRequestException("Account is not active");
        }
    }

    @Override
    public void validateSufficientBalance(BankAccount bankAccount, BigDecimal amount) {
        if (bankAccount.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance. Account: {}, Balance: {}, Required: {}", bankAccount.getAccountNumber(), bankAccount.getBalance(), amount);
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }
}
