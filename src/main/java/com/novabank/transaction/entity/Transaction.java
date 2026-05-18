package com.novabank.transaction.entity;

import com.novabank.account.entity.BankAccount;
import com.novabank.account.enums.TransactionType;
import com.novabank.auth.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false
    )
    private TransactionType transactionType;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bank_account_id",
            nullable = false
    )
    private BankAccount bankAccount;
}
