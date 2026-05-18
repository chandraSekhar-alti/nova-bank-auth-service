package com.novabank.transaction.repository;

import com.novabank.account.entity.BankAccount;
import com.novabank.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction>
    findByBankAccountOrderByCreatedAtDesc(
            BankAccount bankAccount
    );
}