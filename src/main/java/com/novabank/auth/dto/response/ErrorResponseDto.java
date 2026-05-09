package com.novabank.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {

    private boolean success;

    private String message;

    private LocalDateTime timestamp;

    private Map<String, String> errors;
}