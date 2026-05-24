package com.novabank.customer.repo;

import com.novabank.customer.entity.CustomerProfile;
import com.novabank.customer.entity.KycDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycDetailsRepository extends JpaRepository<KycDetails, Long> {

    Optional<KycDetails> findByCustomerProfile(CustomerProfile customerProfile);

    boolean existsByPanNumber(String panNumber);

    boolean existsByAadhaarNumber(String aadhaarNumber);
}
