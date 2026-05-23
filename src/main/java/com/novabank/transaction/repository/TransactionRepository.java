package com.novabank.transaction.repository;

import com.novabank.account.entity.BankAccount;
import com.novabank.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Page<Transaction>
    findByBankAccountOrderByCreatedAtDesc(
            BankAccount bankAccount,
            Pageable pageable
    );
}