package com.novabank.auth.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordResponseDto {

    private String resetToken;
}
