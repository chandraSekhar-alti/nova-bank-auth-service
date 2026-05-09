package com.novabank.auth.mapper;

import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.UserProfileResponseDto;
import com.novabank.auth.entity.AccountStatus;
import com.novabank.auth.entity.User;

public class UserMapper {

    private UserMapper(){
    }

    public static User toEntity(RegisterRequestDto dto){
        User user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobileNumber(dto.getMobileNumber());

        user.setStatus(AccountStatus.ACTIVE);

        return user;
    }

    public static UserProfileResponseDto toUserProfileResponse(User user){
        UserProfileResponseDto response = new UserProfileResponseDto();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setMobileNumber(user.getMobileNumber());
        response.setStatus(user.getStatus());

        return response;
    }
}
