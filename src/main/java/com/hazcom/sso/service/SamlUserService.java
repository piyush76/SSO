package com.hazcom.sso.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SamlUserService {
    private static final Logger logger = LoggerFactory.getLogger(SamlUserService.class);

    public UserDetails createUserDetails(Saml2Authentication authentication) {
        try {
            String email = authentication.getName();
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // Extract additional attributes from SAML response
            var attributes = authentication.getSaml2Response().getAttributes();
            String firstName = getAttributeValue(attributes, "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname");
            String lastName = getAttributeValue(attributes, "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
            
            logger.info("Created user details for: {} {} ({})", firstName, lastName, email);
            
            return new User(email, "", authorities);
        } catch (Exception e) {
            logger.error("Error creating user details from SAML authentication", e);
            throw e;
        }
    }

    private String getAttributeValue(List<Object> attributes, String name) {
        return attributes.stream()
            .filter(attr -> attr instanceof SimpleGrantedAuthority)
            .map(attr -> (SimpleGrantedAuthority) attr)
            .filter(attr -> attr.getAuthority().startsWith(name))
            .map(attr -> attr.getAuthority().substring(name.length() + 1))
            .findFirst()
            .orElse("");
    }
}
