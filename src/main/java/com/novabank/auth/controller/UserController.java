package com.novabank.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/profile")
    public String getProfile(
            Authentication authentication
    ) {

        return "Welcome "
                + authentication.getName();
    }
}