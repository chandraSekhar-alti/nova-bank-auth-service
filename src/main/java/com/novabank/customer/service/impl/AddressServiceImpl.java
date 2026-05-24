package com.novabank.customer.service.impl;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import com.novabank.common.exceptions.ResourceNotFoundException;
import com.novabank.customer.dto.request.AddAddressRequestDto;
import com.novabank.customer.dto.response.AddressResponseDto;
import com.novabank.customer.entity.Address;
import com.novabank.customer.entity.CustomerProfile;
import com.novabank.customer.mapper.AddressMapper;
import com.novabank.customer.repo.AddressRepository;
import com.novabank.customer.repo.CustomerProfileRepository;
import com.novabank.customer.service.customerService.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final CustomerProfileRepository customerProfileRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public ApiResponseDto<AddressResponseDto>
    addAddress(
            AddAddressRequestDto requestDto,
            String userEmail
    ) {
        log.info("Adding address for user with email: {}", userEmail);

        User user = userRepository.
                findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + userEmail)
                );

        CustomerProfile customerProfile =
                customerProfileRepository.
                        findByUser(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Customer profile not found for user with email: " + userEmail)
                        );

        Address address = new Address();

        address.setCustomerProfile(customerProfile);
        address.setAddressType(requestDto.getAddressType());
        address.setAddressLine1(requestDto.getAddressLine1());
        address.setAddressLine2(requestDto.getAddressLine2());
        address.setCity(requestDto.getCity());
        address.setState(requestDto.getState());
        address.setCountry(requestDto.getCountry());
        address.setPinCode(requestDto.getPinCode());

        Address savedAddress = addressRepository.save(address);

        log.info("Address added successfully for user with email: {}", userEmail);

        return new ApiResponseDto<>(
                true,
                "Address added successfulyy",
                addressMapper.toResponseDto(savedAddress)
        );
    }
}
