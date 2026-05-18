package com.novabank.account.repository;

import com.novabank.account.entity.BankAccount;
import com.novabank.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findByUser(User user);

    boolean existsByAccountNumber(String accountNumber);

}
