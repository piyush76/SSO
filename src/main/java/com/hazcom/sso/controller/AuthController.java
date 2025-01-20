package com.hazcom.sso.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/auth/user")
    public Map<String, Object> user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", authentication.getName());
        userDetails.put("authorities", authentication.getAuthorities());
        return userDetails;
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to Hazcom SSO Service";
    }
}
