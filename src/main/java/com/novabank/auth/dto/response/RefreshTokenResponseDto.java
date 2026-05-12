package com.novabank.auth.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenResponseDto {

    private String accessToken;

    private String tokenType = "Bearer";

    private Long expiresIn;
}
