package com.novabank.auth.repository;

import com.novabank.auth.entity.User;
import com.novabank.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);
}