package com.novabank.auth.service.impl;


import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.Role;
import com.novabank.auth.entity.RoleType;
import com.novabank.auth.entity.User;
import com.novabank.auth.entity.UserRole;
import com.novabank.auth.exception.MobileNumberAlreadyExistsException;
import com.novabank.auth.exception.UserAlreadyExistsException;
import com.novabank.auth.mapper.UserMapper;
import com.novabank.auth.repository.RoleRepository;
import com.novabank.auth.repository.UserRepository;
import com.novabank.auth.repository.UserRoleRepository;
import com.novabank.auth.service.AuthService.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ApiResponseDto<String> registerUser(
            RegisterRequestDto registerRequestDto
    ) {
        //Checking if email is already exists
        if(userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already exists: " + registerRequestDto.getEmail()
            );
        }

        //Checking if Mobile Number is already exists
        if(userRepository.existsByMobileNumber(registerRequestDto.getMobileNumber())){
            throw new MobileNumberAlreadyExistsException(
                    "Mobile number already exists: " + registerRequestDto.getMobileNumber()
            );
        }

        if(!registerRequestDto.getPassword().equals(registerRequestDto.getConfirmPassword())){
            return new ApiResponseDto<>(
                    false,
                    "Password and Confirm Password do not match",
                    null
            );
        }

        User user = UserMapper.toEntity(registerRequestDto);

        user.setPasswordHash(
                passwordEncoder.encode(
                        registerRequestDto.getPassword()
                )
        );

        User savedUser = userRepository.save(user);

        Role userRole = roleRepository
                .findByRoleName(RoleType.USER)
                .orElseThrow(() ->
                        new RuntimeException("Default role not found")
                );

        UserRole userRoleMapping = new UserRole();
        userRoleMapping.setUser(savedUser);
        userRoleMapping.setRole(userRole);

        userRoleRepository.save(userRoleMapping);

        return new ApiResponseDto<>(
                true,
                "User registered successfully",
                null
        );
    }
}
