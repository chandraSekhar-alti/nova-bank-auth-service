package com.novabank.auth.dto.response;


import com.novabank.auth.entity.AccountStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponseDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobileNumber;

    private AccountStatus status;

}
