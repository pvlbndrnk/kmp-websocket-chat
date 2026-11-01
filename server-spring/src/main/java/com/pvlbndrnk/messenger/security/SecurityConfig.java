package com.pvlbndrnk.messenger.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(HttpMethod.POST, "/users").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/messages").permitAll();
            auth.requestMatchers("/chat-socket").permitAll();
            auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
            auth.anyRequest().denyAll();
        });
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
