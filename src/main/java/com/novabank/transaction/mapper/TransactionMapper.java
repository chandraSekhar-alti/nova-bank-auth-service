package com.novabank.transaction.mapper;

import com.novabank.transaction.dto.response.TransactionResponseDto;
import com.novabank.transaction.entity.Transaction;

public class TransactionMapper {
    private TransactionMapper(){
    }

    public static TransactionResponseDto toTransactionResponse(
            Transaction transaction
    ){
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setTransactionTYpe(transaction.getTransactionType().name());
        responseDto.setAmount(transaction.getAmount());
        responseDto.setDescription(transaction.getDescription());
        responseDto.setCreatedAt(transaction.getCreatedAt().toString());
        return responseDto;
    }
}
