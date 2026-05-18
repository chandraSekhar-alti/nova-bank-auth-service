package com.novabank.account.controller;

import com.novabank.account.dto.request.CreateBankAccountRequestDto;
import com.novabank.account.dto.request.DepositRequestDto;
import com.novabank.account.dto.request.TransferRequestDto;
import com.novabank.account.dto.request.WithdrawRequestDto;
import com.novabank.account.dto.response.BankAccountResponseDto;
import com.novabank.account.service.AccountService.AccountService;
import com.novabank.auth.dto.response.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<
            ApiResponseDto<BankAccountResponseDto>
            > createAccount(
            @Valid
            @RequestBody
            CreateBankAccountRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        ApiResponseDto<BankAccountResponseDto>
                response =
                accountService.createBankAccount(
                        requestDto,
                        userEmail
                );

        return ResponseEntity.ok(response);

    }


    @GetMapping
    public ResponseEntity<
            ApiResponseDto<List<BankAccountResponseDto>>
            > getUserAccounts(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        ApiResponseDto<List<BankAccountResponseDto>>
                response =
                accountService.fetchALlBankAccounts(
                        userEmail
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<
            ApiResponseDto<BankAccountResponseDto>
            > depositMoney(
            @Valid
            @RequestBody
            DepositRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        ApiResponseDto<BankAccountResponseDto>
                response =
                accountService.depositMoney(
                        requestDto,
                        userEmail
                );

        return ResponseEntity.ok(response);

    }

    @PostMapping("withdraw")
    public ResponseEntity<
            ApiResponseDto<BankAccountResponseDto>
            > withdrawMoney(
            @Valid
            @RequestBody
            WithdrawRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        ApiResponseDto<BankAccountResponseDto>
                response =
                accountService.withdrawMoney(
                        requestDto,
                        userEmail
                );

        return ResponseEntity.ok(response);

    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponseDto<String>>
    transferMoney(
            @Valid
            @RequestBody
            TransferRequestDto requestDto,
            Authentication authentication
    ) {

        String userEmail =
                authentication.getName();

        ApiResponseDto<String> response =
                accountService.transferMoney(
                        requestDto,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }

}
