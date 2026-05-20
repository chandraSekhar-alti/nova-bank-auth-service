package com.novabank.common.exceptions;


import com.novabank.auth.dto.response.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleResourceNotFoundException(
            ResourceNotFoundException e
    ) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponseDto<>(
                                false,
                                e.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleUnauthorizedException(
            UnauthorizedException e
    ) {
        log.warn("Unauthorized access: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiResponseDto<>(
                                false,
                                e.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleAccountNotFoundException(
            AccountNotFoundException e
    ) {
        log.warn("Account not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponseDto<>(
                                false,
                                e.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler({InvalidAccountAccessException.class, ForbiddenException.class})
    public ResponseEntity<ApiResponseDto<Object>> handleAccessDeniedException(
            RuntimeException e
    ) {
        log.error("Access denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        new ApiResponseDto<>(
                                false,
                                e.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleInsufficientBalanceException(
            InsufficientBalanceException e
    ) {
        log.warn("Insufficient balance: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ApiResponseDto<>(
                                false,
                                e.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiResponseDto<Object>>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {


        var fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation failed";

        log.warn("Validation failed: {}", errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ApiResponseDto<>(
                                false,
                                errorMessage,
                                null
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>>
    handleGlobalException(
            Exception ex
    ) {

        // Log full stacktrace for unexpected errors to assist with debugging and monitoring
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ApiResponseDto<>(
                                false,
                                "An unexpected error occurred",
                                null
                        )
                );
    }

}
