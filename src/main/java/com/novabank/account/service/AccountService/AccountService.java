package com.novabank.account.service.AccountService;

import com.novabank.account.dto.request.CreateBankAccountRequestDto;
import com.novabank.account.dto.request.DepositRequestDto;
import com.novabank.account.dto.request.TransferRequestDto;
import com.novabank.account.dto.request.WithdrawRequestDto;
import com.novabank.account.dto.response.BankAccountResponseDto;
import com.novabank.auth.dto.response.ApiResponseDto;

import java.util.List;

public interface AccountService {

    ApiResponseDto<BankAccountResponseDto>
    createBankAccount(
            CreateBankAccountRequestDto requestDto,
            String userEmail
    );

    ApiResponseDto<List<BankAccountResponseDto>>
    fetchALlBankAccounts(
            String userEmail
    );

    ApiResponseDto<BankAccountResponseDto>
    depositMoney(
            DepositRequestDto requestDto,
            String userEmail
    );

    ApiResponseDto<BankAccountResponseDto>
    withdrawMoney(
            WithdrawRequestDto requestDto,
            String userEmail
    );

    ApiResponseDto<String>
    transferMoney(
            TransferRequestDto requestDto,
            String userEmail
    );
}
