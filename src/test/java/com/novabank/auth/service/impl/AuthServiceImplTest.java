package com.novabank.auth.service.impl;


import com.novabank.auth.dto.request.RegisterRequestDto;
import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.Role;
import com.novabank.auth.entity.RoleType;
import com.novabank.auth.entity.User;
import com.novabank.auth.entity.UserRole;
import com.novabank.common.exceptions.MobileNumberAlreadyExistsException;
import com.novabank.common.exceptions.UserAlreadyExistsException;
import com.novabank.auth.repository.*;
import com.novabank.auth.security.jwt.JwtService;
import com.novabank.auth.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDto createValidRegisterRequest() {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setFirstName("Jhon");
        requestDto.setLastName("Don");
        requestDto.setEmail("jhon.don@gmail.com");
        requestDto.setPassword("password123");
        requestDto.setConfirmPassword("password123");
        requestDto.setMobileNumber("1234567890");

        Role role = new Role();
        role.setRoleName(RoleType.USER);
        role.setId(1L);

        return requestDto;
    }

    private Role createUserRole() {

        Role role = new Role();
        role.setId(1L);
        role.setRoleName(RoleType.USER);

        return role;
    }

    @DisplayName("Register user Tests")
    @Nested
    class RegisterUserTests {

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Arrange
            RegisterRequestDto registerRequestDto = new RegisterRequestDto();
            registerRequestDto.setEmail("test@gmail.com");

            when(userRepository.existsByEmail(registerRequestDto.getEmail()))
                    .thenReturn(true);

            // Act + Assert
            assertThrows(
                    UserAlreadyExistsException.class,
                    () -> authService.registerUser(registerRequestDto)
            );

            //Verify
            verify(userRepository)
                    .existsByEmail(registerRequestDto.getEmail());
        }

        @Test
        @DisplayName("Should throw exception when mobile number already exists")
        void shouldThrowExceptionWhenMobileNumberAlreadyExists() {
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setMobileNumber("1234567890");

            when(userRepository.existsByMobileNumber(requestDto.getMobileNumber()))
                    .thenReturn(true);

            assertThrows(
                    MobileNumberAlreadyExistsException.class,
                    () -> authService.registerUser(requestDto)
            );

            verify(userRepository)
                    .existsByMobileNumber(requestDto.getMobileNumber());
        }

        @Test
        @DisplayName("Should return error response when password and confirm password do not match")
        void shouldReturnErrorResponseWhenPasswordAndConfirmPasswordDoNotMatch() {
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setPassword("password123");
            requestDto.setMobileNumber("1234567890");
            requestDto.setPassword("password123");
            requestDto.setConfirmPassword("password456");

            when(userRepository.existsByEmail(
                    requestDto.getEmail())
            ).thenReturn(false);
            when(userRepository.existsByMobileNumber(
                    requestDto.getMobileNumber())
            ).thenReturn(false);

            ApiResponseDto<String> response = authService.registerUser(requestDto);

            assertFalse(response.isSuccess());
            assertEquals("Password and Confirm Password do not match", response.getMessage());

            assertNull(response.getData());

            verify(userRepository).existsByEmail(requestDto.getEmail());
            verify(userRepository).existsByMobileNumber(requestDto.getMobileNumber());
        }

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() {
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setEmail("Jhon.don@gmail.com");
            requestDto.setFirstName("Jhon");
            requestDto.setLastName("Don");
            requestDto.setMobileNumber("1234567890");
            requestDto.setPassword("password123");
            requestDto.setConfirmPassword("password123");

            Role role = new Role();
            role.setRoleName(RoleType.USER);

            User saveUser = new User();
            saveUser.setId(1L);
            saveUser.setEmail(requestDto.getEmail());

            when(userRepository.existsByEmail(requestDto.getEmail()))
                    .thenReturn(false);

            when(userRepository.existsByMobileNumber(requestDto.getMobileNumber()))
                    .thenReturn(false);

            when(passwordEncoder.encode(requestDto.getPassword()))
                    .thenReturn("encodedPassword");

            when(roleRepository.findByRoleName(RoleType.USER))
                    .thenReturn(Optional.of(role));

            when(userRepository.save(any(User.class)))
                    .thenReturn(saveUser);

            ApiResponseDto<String> response = authService.registerUser(requestDto);

            assertNotNull(response);
            assertTrue(response.isSuccess());
            assertEquals("User registered successfully", response.getMessage());

            verify(userRepository).existsByEmail(requestDto.getEmail());
            verify(userRepository).existsByMobileNumber(requestDto.getMobileNumber());
            verify(passwordEncoder).encode(requestDto.getPassword());
            verify(roleRepository).findByRoleName(RoleType.USER);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Role not found")
        void shouldThrowExceptionWhenRoleNotFound() {
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setMobileNumber("1234567890");
            requestDto.setPassword("password123");
            requestDto.setConfirmPassword("password123");
            requestDto.setFirstName("Jhon");
            requestDto.setLastName("Don");
            requestDto.setEmail("jhon.don@gmail.com");

            when(userRepository.existsByMobileNumber(requestDto.getMobileNumber()))
                    .thenReturn(false);

            when(userRepository.existsByEmail(requestDto.getEmail())
            ).thenReturn(false);

            when(passwordEncoder.encode(requestDto.getPassword())
            ).thenReturn("encodedPassword");

            when(roleRepository.findByRoleName(RoleType.USER))
                    .thenReturn(Optional.empty());

            // Act + Assert
            RuntimeException exception =
                    assertThrows(RuntimeException.class,
                            () -> authService.registerUser(requestDto));

            assertEquals("Default role not found", exception.getMessage());
        }

        @Test
        @DisplayName("Password Encoding verification")
        void shouldVerifyPasswordEncoding() {
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setFirstName("Jhon");
            requestDto.setLastName("Don");
            requestDto.setEmail("jhon.don@gmail.com");
            requestDto.setPassword("password123");
            requestDto.setConfirmPassword("password123");
            requestDto.setMobileNumber("1234567890");

            Role role = new Role();
            role.setRoleName(RoleType.USER);
            role.setId(1L);

            User savedUser = new User();
            savedUser.setId(1L);

            when(userRepository.existsByEmail(requestDto.getEmail()))
                    .thenReturn(false);

            when(userRepository.existsByMobileNumber(requestDto.getMobileNumber()))
                    .thenReturn(false);

            when(passwordEncoder.encode(requestDto.getPassword()))
                    .thenReturn("encodedPassword");

            when(roleRepository.findByRoleName(RoleType.USER))
                    .thenReturn(Optional.of(role));

            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            authService.registerUser(requestDto);
            verify(passwordEncoder).encode(requestDto.getPassword());
        }

        @Test
        @DisplayName("User save Failure")
        void shouldHandleUserSaveFailure() {
            //Arrange
            RegisterRequestDto requestDto = createValidRegisterRequest();
            Role role = createUserRole();

            when(userRepository.existsByEmail(requestDto.getEmail()))
                    .thenReturn(false);
            when(userRepository.existsByMobileNumber(requestDto.getMobileNumber()))
                    .thenReturn(false);
            when(passwordEncoder.encode(requestDto.getPassword()))
                    .thenReturn("encodedPassword");
//            when(roleRepository.findByRoleName(
//                    RoleType.USER
//            )).thenReturn(Optional.of(role));
            when(userRepository.save(any(User.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // Act + Assert
            RuntimeException exception =
                    assertThrows(
                            RuntimeException.class,
                            () -> authService.registerUser(requestDto)
                    );

            assertEquals(
                    "Database error",
                    exception.getMessage()
            );

            // Verify
            verify(userRepository)
                    .save(any(User.class));

            verify(userRoleRepository, never())
                    .save(any(UserRole.class));
        }

        // Have to implement later
//        @Test
//        @DisplayName("UserRole mapping failure")
//        void shouldHandleUserRoleMappingFailure() {
//
//        }
//
//        @Test
//        @DisplayName("Verify No unnecessary calls")
//        void shouldVerifyNoUnnecessaryCalls() {
//        }
//
//        @Test
//        @DisplayName("Verify No save on validation failure")
//        void shouldVerifyNoSaveOnValidationFailure() {
//
//        }


    }
}





