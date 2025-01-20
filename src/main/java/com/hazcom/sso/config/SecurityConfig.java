package com.hazcom.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import com.hazcom.sso.service.SamlUserService;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SamlUserService samlUserService;

    public SecurityConfig(SamlUserService samlUserService) {
        this.samlUserService = samlUserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(responseToken -> {
            var authentication = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter()
                .convert(responseToken);
            
            if (authentication == null) {
                throw new RuntimeException("Failed to authenticate SAML response");
            }

            return authentication;
        });

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
            )
            .saml2Login(saml2 -> saml2
                .authenticationManager(auth -> authenticationProvider.authenticate(auth))
                .loginProcessingUrl("${app.sso.endpoint-url}")
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
