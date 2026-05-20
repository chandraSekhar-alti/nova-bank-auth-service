package com.novabank.account.mapper;

import com.novabank.account.dto.response.BankAccountResponseDto;
import com.novabank.account.entity.BankAccount;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    private BankAccountMapper() {
    }

    public BankAccountResponseDto toResponseDto(BankAccount bankAccount) {
        BankAccountResponseDto responseDto = new BankAccountResponseDto();

        responseDto.setAccountNumber(bankAccount.getAccountNumber());
        responseDto.setAccountType(bankAccount.getAccountType());
        responseDto.setAccountStatus(bankAccount.getAccountStatus());
        responseDto.setBalance(bankAccount.getBalance());

        return responseDto;
    }
}
