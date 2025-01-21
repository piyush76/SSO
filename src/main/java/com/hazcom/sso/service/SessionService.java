package com.hazcom.sso.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    
    @Value("${app.session.timeout}")
    private String sessionTimeout;
    
    public String createSession(String email) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new SessionInfo(email, Instant.now()));
        logger.info("Created session for user: {}", email);
        return token;
    }
    
    public boolean validateSession(String token) {
        SessionInfo session = sessions.get(token);
        if (session == null) {
            return false;
        }
        
        Duration timeout = Duration.parse("PT" + sessionTimeout.toUpperCase());
        if (Instant.now().isAfter(session.createdAt.plus(timeout))) {
            sessions.remove(token);
            logger.info("Session expired for user: {}", session.email);
            return false;
        }
        
        return true;
    }
    
    private static class SessionInfo {
        final String email;
        final Instant createdAt;
        
        SessionInfo(String email, Instant createdAt) {
            this.email = email;
            this.createdAt = createdAt;
        }
    }
}
