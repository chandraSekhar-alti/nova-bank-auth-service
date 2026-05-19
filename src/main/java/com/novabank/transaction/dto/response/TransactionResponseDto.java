package com.novabank.transaction.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionResponseDto {
    private String transactionTYpe;

    private BigDecimal amount;

    private String description;

    private String createdAt;
}
