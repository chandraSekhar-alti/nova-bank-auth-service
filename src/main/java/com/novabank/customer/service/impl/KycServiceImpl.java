package com.novabank.customer.service.impl;

import com.novabank.auth.dto.response.ApiResponseDto;
import com.novabank.auth.entity.User;
import com.novabank.auth.repository.UserRepository;
import com.novabank.common.exceptions.BadRequestException;
import com.novabank.common.exceptions.ResourceNotFoundException;
import com.novabank.customer.dto.request.AddKycRequestDto;
import com.novabank.customer.dto.response.KycDetailsResponseDto;
import com.novabank.customer.entity.CustomerProfile;
import com.novabank.customer.entity.KycDetails;
import com.novabank.customer.enums.KycStatus;
import com.novabank.customer.mapper.KycDetailsMapper;
import com.novabank.customer.repo.AddressRepository;
import com.novabank.customer.repo.CustomerProfileRepository;
import com.novabank.customer.repo.KycDetailsRepository;
import com.novabank.customer.service.customerService.KycService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final KycDetailsMapper kycDetailsMapper;

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final KycDetailsRepository kycDetailsRepository;

    @Override
    public ApiResponseDto<KycDetailsResponseDto
            > addKycDetails(
            AddKycRequestDto requestDto,
            String userEmail
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomerProfile customerProfile = customerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        if(kycDetailsRepository.findByCustomerProfile(customerProfile).isPresent()){
            throw new BadRequestException("KYC details already exist for this customer");
        }

        if(kycDetailsRepository.existsByPanNumber(requestDto.getPanNumber())){
            throw new BadRequestException("PAN number already exists");
        }

        if (kycDetailsRepository.existsByAadhaarNumber(requestDto.getAadhaarNumber())) {
            throw new BadRequestException("Aadhaar number already exists");
        }

        KycDetails kycDetails = new KycDetails();

        kycDetails.setCustomerProfile(customerProfile);
        kycDetails.setPanNumber(requestDto.getPanNumber());
        kycDetails.setAadhaarNumber(requestDto.getAadhaarNumber());
        kycDetails.setKycStatus(KycStatus.PENDING);
        kycDetails.setPanVerified(false);
        kycDetails.setAadhaarVerified(false);

        KycDetails saveKyc = kycDetailsRepository.save(kycDetails);

        log.info( "KYC submitted successfully for user: {}", userEmail);

        return new ApiResponseDto<>(
                true,
                "kyc submitted successfully",
                kycDetailsMapper.toResponse(saveKyc)
        );

    }
}
