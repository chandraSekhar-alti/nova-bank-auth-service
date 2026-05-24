package com.novabank.customer.service.impl;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import com.novabank.common.exceptions.BadRequestException;
import com.novabank.common.exceptions.ResourceNotFoundException;
import com.novabank.customer.dto.request.CompleteCustomerProfileRequestDto;
import com.novabank.customer.dto.response.CustomerProfileResponseDto;
import com.novabank.customer.entity.CustomerProfile;
import com.novabank.customer.enums.Gender;
import com.novabank.customer.enums.MaritalStatus;
import com.novabank.customer.mapper.CustomerProfileMapper;
import com.novabank.customer.repo.AddressRepository;
import com.novabank.customer.repo.CustomerProfileRepository;
import com.novabank.customer.service.customerService.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerProfileMapper customerProfileMapper;

    @Override
    public ApiResponseDto<CustomerProfileResponseDto> completeProfile(
            CompleteCustomerProfileRequestDto requestDto,
            String userEmail
    ){
        log.info("Completing customer profile for user with email: {}", userEmail);

        User user = userRepository.
                findByEmail(userEmail)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "User not found with email: " + userEmail)
                );

        if(customerProfileRepository.findByUser(user).isPresent()){
            log.warn("Customer profile already exists for user with email: {}", userEmail);
            throw new BadRequestException(
                    "Customer profile already exists for user with email: " + userEmail
            );
        }

        CustomerProfile customerProfile = new CustomerProfile();

        customerProfile.setUser(user);
        customerProfile.setFirstName(requestDto.getFirstName());
        customerProfile.setLastName(requestDto.getLastName());
        customerProfile.setGender(Gender.valueOf(requestDto.getGender()));
        customerProfile.setMaritalStatus(MaritalStatus.valueOf(requestDto.getMaritalStatus()));
        customerProfile.setFatherOrSpouseName(requestDto.getFatherOrSpouseName());
        customerProfile.setEmploymentType(requestDto.getEmploymentType());
        customerProfile.setAnnualIncome(requestDto.getAnnualIncome());
        customerProfile.setDateOfBirth(requestDto.getDateOfBirth());

        CustomerProfile savedProfile = customerProfileRepository.save(customerProfile);

        log.info("Customer profile created successfully for user with email: {}", userEmail);

        return new ApiResponseDto<>(
                true,
                "Customer profile completed successfully",
                customerProfileMapper.toCustomerProfileResponseDto(savedProfile)
        );
    }

    
}
