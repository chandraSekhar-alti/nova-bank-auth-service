package com.novabank.transaction.service.impl;

import com.novabank.account.entity.BankAccount;
import com.novabank.account.enums.TransactionType;
import com.novabank.transaction.entity.Transaction;
import com.novabank.transaction.repository.TransactionRepository;
import com.novabank.transaction.service.TramsactionService.TransactionAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAuditServiceImpl implements TransactionAuditService {

    private final TransactionRepository transactionRepository;

    @Override
    public void createTransaction(
            BankAccount bankAccount,
            TransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        Transaction transaction = new Transaction();

        transaction.setBankAccount(bankAccount);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        transactionRepository.save(transaction);

        log.info("Transaction created: {} {} for account {}", transactionType, amount, bankAccount.getAccountNumber());
    }
}
