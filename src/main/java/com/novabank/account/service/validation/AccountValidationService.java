package com.novabank.account.service.validation;

import com.novabank.account.entity.BankAccount;
import com.novabank.auth.entity.User;

import java.math.BigDecimal;

public interface AccountValidationService {

    void validateAccountOwnership(
            BankAccount bankAccount,
            User user
    );

    void validateAccountStatusIsActive(
            BankAccount bankAccount
    );

    void validateSufficientBalance(
            BankAccount bankAccount,
            BigDecimal amount
    );
}
