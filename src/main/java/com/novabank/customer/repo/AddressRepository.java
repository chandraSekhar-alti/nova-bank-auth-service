package com.novabank.customer.repo;

import com.novabank.customer.entity.Address;
import com.novabank.customer.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByCustomerProfile(CustomerProfile customerProfile);
}
