package com.novabank.account.dto.response;

import com.novabank.account.enums.AccountStatus;
import com.novabank.account.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BankAccountResponseDto {

    private String accountNumber;

    private AccountType accountType;

    private AccountStatus accountStatus;

    private BigDecimal balance;
}
