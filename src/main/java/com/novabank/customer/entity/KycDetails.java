package com.novabank.customer.entity;


import com.novabank.customer.enums.KycStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "kyc_details")
@Getter
@Setter
public class KycDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_profile_id",
            nullable = false,
            unique = true
    )
    private CustomerProfile customerProfile;

    @Column(name = "aadhaar_number", nullable = false, unique = true)
    private String aadhaarNumber;

    @Column(name = "pan_number", nullable = false, unique = true)
    private String panNumber;

    @Column(name = "kyc_status")
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    @Column(name = "pan_verified")
    private boolean panVerified = false;

    @Column(name = "aadhaar_verified")
    private boolean aadhaarVerified = false;
}
