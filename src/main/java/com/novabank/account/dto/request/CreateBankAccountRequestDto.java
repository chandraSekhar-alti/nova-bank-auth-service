package com.novabank.account.dto.request;

import com.novabank.account.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBankAccountRequestDto {

    @NotNull(message = "Account type is required")
    private AccountType accountType;
}
