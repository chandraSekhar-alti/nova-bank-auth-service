package com.novabank.auth.config;

import com.novabank.auth.entity.Role;
import com.novabank.auth.entity.RoleType;
import com.novabank.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRoleIfNotExists(
                RoleType.USER,
                "Default user role"
        );

        createRoleIfNotExists(
                RoleType.ADMIN,
                "Administrator role"
        );

        createRoleIfNotExists(
                RoleType.SUPPORT_AGENT,
                "Support agent role"
        );
    }

    private void createRoleIfNotExists(
            RoleType roleType,
            String description
    ) {

        boolean roleExists =
                roleRepository.findByRoleName(roleType)
                        .isPresent();

        if (!roleExists) {

            Role role = new Role();

            role.setRoleName(roleType);
            role.setDescription(description);

            roleRepository.save(role);
        }
    }
}