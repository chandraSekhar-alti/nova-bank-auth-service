package com.novabank.transaction.service.TramsactionService;

import com.novabank.account.entity.BankAccount;
import com.novabank.account.enums.TransactionType;

import java.math.BigDecimal;

public interface TransactionAuditService {

    void createTransaction(
            BankAccount bankAccount,
            TransactionType transactionType,
            BigDecimal amount,
            String description
    );
}
