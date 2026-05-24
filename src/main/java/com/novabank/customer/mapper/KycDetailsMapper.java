package com.novabank.customer.mapper;

import com.novabank.customer.dto.response.KycDetailsResponseDto;
import com.novabank.customer.entity.KycDetails;
import org.springframework.stereotype.Component;

@Component
public class KycDetailsMapper {

    public KycDetailsResponseDto toResponse(
            KycDetails kycDetails
    ){
        KycDetailsResponseDto response = new KycDetailsResponseDto();

        response.setPanNumber(kycDetails.getPanNumber());
        response.setMaskedAadhaarNumber(maskAadhaar(
                kycDetails.getAadhaarNumber()
        ));

        response.setKycStatus(kycDetails.getKycStatus());
        response.setPanVerified(kycDetails.isPanVerified());
        response.setAadhaarVerified(kycDetails.isAadhaarVerified());

        return response;
    }

    private String maskAadhaar(String aadhaarNumber) {
        return "XXXX-XXXX-" + aadhaarNumber.substring(8);
    }
}
