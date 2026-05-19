package com.novabank.auth.service.impl;


import com.novabank.auth.dto.request.*;
import com.novabank.auth.dto.response.*;
import com.novabank.auth.entity.*;
import com.novabank.common.exceptions.MobileNumberAlreadyExistsException;
import com.novabank.common.exceptions.UserAlreadyExistsException;
import com.novabank.auth.mapper.UserMapper;
import com.novabank.auth.repository.*;
import com.novabank.auth.security.jwt.JwtService;
import com.novabank.auth.security.service.CustomUserDetailsService;
import com.novabank.auth.service.AuthService.AuthService;
import com.novabank.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public ApiResponseDto<String> registerUser(
            RegisterRequestDto registerRequestDto
    ) {
        //Checking if email is already exists
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already exists: " + registerRequestDto.getEmail()
            );
        }

        //Checking if Mobile Number is already exists
        if (userRepository.existsByMobileNumber(registerRequestDto.getMobileNumber())) {
            throw new MobileNumberAlreadyExistsException(
                    "Mobile number already exists: " + registerRequestDto.getMobileNumber()
            );
        }

        if (!registerRequestDto.getPassword().equals(registerRequestDto.getConfirmPassword())) {
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

    @Override
    public ApiResponseDto<LoginResponseDto> loginUser(
            LoginRequestDto requestDto
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );

        UserDetails userDetails =
                customUserDetailsService
                        .loadUserByUsername(
                                requestDto.getEmail()
                        );

        String accessToken =
                jwtService.generateToken(userDetails);

        User user = userRepository
                .findByEmail(requestDto.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        String refreshTokenValue =
                java.util.UUID.randomUUID().toString();

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(refreshTokenValue);

        refreshToken.setExpiryDate(
                java.time.LocalDateTime.now()
                        .plusDays(7)
        );

        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);

        LoginResponseDto responseDto =
                new LoginResponseDto();

        responseDto.setAccessToken(accessToken);
        responseDto.setRefreshToken(refreshTokenValue);
        responseDto.setExpiresIn(900L);

        return new ApiResponseDto<>(
                true,
                "Login successful",
                responseDto
        );
    }

    @Override
    public ApiResponseDto<RefreshTokenResponseDto>
    refreshAccessToken(
            RefreshTokenRequestDto requestDto
    ) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(
                                requestDto.getRefreshToken()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid refresh token"
                                )
                        );

        if (refreshToken.getIsRevoked()) {

            throw new RuntimeException(
                    "Refresh token revoked"
            );
        }

        if (refreshToken.getExpiryDate()
                .isBefore(java.time.LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        User user = refreshToken.getUser();

        UserDetails userDetails =
                customUserDetailsService
                        .loadUserByUsername(
                                user.getEmail()
                        );

        String newAccessToken =
                jwtService.generateToken(userDetails);

        RefreshTokenResponseDto responseDto =
                new RefreshTokenResponseDto();

        responseDto.setAccessToken(newAccessToken);
        responseDto.setExpiresIn(900L);

        return new ApiResponseDto<>(
                true,
                "Access token refreshed successfully",
                responseDto
        );
    }

    @Override
    public ApiResponseDto<String> logoutUser(LogoutRequestDto logoutRequestDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(
                        logoutRequestDto.getRefreshToken()
                )
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        refreshToken.setIsRevoked(true);

        refreshTokenRepository.save(refreshToken);

        return new ApiResponseDto<>(
                true,
                "Logout successful",
                null
        );
    }

    @Override
    public ApiResponseDto<ForgotPasswordResponseDto> forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {

        User user = userRepository.findByEmail(
                forgotPasswordRequestDto.getEmail()
        ).orElseThrow(() ->
                new ResourceNotFoundException("User not found with email: " + forgotPasswordRequestDto.getEmail())
        );

        PasswordResetToken passwordResetToken = new PasswordResetToken();

        String token = UUID.randomUUID().toString();

        passwordResetToken.setToken(token);

        passwordResetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );

        passwordResetToken.setUser(user);

        passwordResetTokenRepository.save(passwordResetToken);

        ForgotPasswordResponseDto forgotPasswordResponseDto = new ForgotPasswordResponseDto();
        forgotPasswordResponseDto.setResetToken(token);

        return new ApiResponseDto<>(
                true,
                "Password reset token generated successfully",
                forgotPasswordResponseDto
        );
    }

    @Override
    public ApiResponseDto<String> resetPassword(ResetPasswordRequestDto requestDto) {

        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match! Try Again");
        }

        PasswordResetToken resetToken =
                passwordResetTokenRepository.
                        findByToken(requestDto.getToken())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid password reset token")
                        );

        if(resetToken.getIsUsed()) {
            throw new RuntimeException("Password reset token already used");
        }

        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(
                passwordEncoder.encode(requestDto.getNewPassword())
        );

        userRepository.save(user);
        resetToken.setIsUsed(true);
        passwordResetTokenRepository.save(resetToken);

        return new ApiResponseDto<>(
                true,
                "Password reset successful",
                null
        );
    }
}
