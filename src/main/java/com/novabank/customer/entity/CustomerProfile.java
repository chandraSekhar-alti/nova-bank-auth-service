package com.novabank.customer.entity;


import com.novabank.auth.entity.BaseEntity;
import com.novabank.auth.entity.User;
import com.novabank.customer.enums.EmploymentType;
import com.novabank.customer.enums.Gender;
import com.novabank.customer.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "customer_profiles")
public class CustomerProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "father_or_spouse_name")
    private String fatherOrSpouseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Column(
            name = "annual_income",
            precision = 19,
            scale = 2
    )
    private BigDecimal annualIncome;

}
