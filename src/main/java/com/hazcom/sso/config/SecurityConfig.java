package com.hazcom.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import com.hazcom.sso.service.SamlUserService;
import static org.springframework.security.config.Customizer.withDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final SamlUserService samlUserService;

    public SecurityConfig(SamlUserService samlUserService) {
        this.samlUserService = samlUserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(responseToken -> {
            var defaultConverter = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();
            var authentication = defaultConverter.convert(responseToken);
            
            if (authentication == null) {
                throw new RuntimeException("Failed to authenticate SAML response");
            }

            var userDetails = samlUserService.createUserDetails(authentication);
            return authentication;
        });

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/maxcomsc/login").permitAll()
                .requestMatchers("/maxcomsc/auth-success").authenticated()
                .anyRequest().authenticated()
            )
            .saml2Login(saml2 -> saml2
                .authenticationManager(auth -> authenticationProvider.authenticate(auth))
                .loginProcessingUrl("${app.sso.endpoint-url}")
                .successHandler((request, response, authentication) -> {
                    response.sendRedirect("/sso/maxcomsc/auth-success");
                })
                .failureHandler((request, response, exception) -> {
                    logger.error("SAML authentication failed", exception);
                    response.sendRedirect("/sso/login?error=authentication_failed");
                })
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired")
                .and()
                .sessionFixation(fixation -> fixation.migrateSession())
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login?invalid")
            )
            .saml2Logout(withDefaults());
            
        return http.build();
    }
}
