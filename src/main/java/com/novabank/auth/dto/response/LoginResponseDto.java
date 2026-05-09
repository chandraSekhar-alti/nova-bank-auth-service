package com.novabank.auth.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

    private String accessToken;

    private String refreshToken;

    private String tokenType = "Bearer";

    private Long expiresIn;
}
