package com.hazcom.sso.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import com.hazcom.sso.service.SessionService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/maxcomsc")
public class MaxcomscAuthController {
    private static final Logger logger = LoggerFactory.getLogger(MaxcomscAuthController.class);

    @Value("${cors.allowed-origins}")
    private String maxcomscUrl;

    @GetMapping("/login")
    public RedirectView login() {
        logger.info("Redirecting to SAML SSO login");
        return new RedirectView("/sso/saml2/authenticate/azure-ad");
    }

    @GetMapping("/auth-success")
    public RedirectView authSuccess(Authentication authentication, HttpServletResponse response) {
        logger.info("Authentication successful for user: {}", authentication.getName());
        
        if (authentication instanceof Saml2Authentication) {
            Saml2Authentication saml2Auth = (Saml2Authentication) authentication;
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) saml2Auth.getPrincipal();
            
            // Extract user attributes
            String email = principal.getName();
            String firstName = principal.getFirstAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname");
            String lastName = principal.getFirstAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
            
            // Create session cookie for maxcomsc
            Cookie sessionCookie = new Cookie("MAXCOMSC_SSO_SESSION", generateSessionToken(email));
            sessionCookie.setDomain("app.maxcomsc.com");
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);
            response.addCookie(sessionCookie);
            
            logger.info("Created session for user: {} {} ({})", firstName, lastName, email);
        }
        
        return new RedirectView(maxcomscUrl + "?sso=true");
    }
    
    private final SessionService sessionService;
    
    public MaxcomscAuthController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    
    private String generateSessionToken(String email) {
        return sessionService.createSession(email);
    }
}
