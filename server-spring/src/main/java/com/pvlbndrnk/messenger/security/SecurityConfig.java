package com.pvlbndrnk.messenger.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 19 * 1024, 2);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(HttpMethod.POST, "/users").permitAll();
            auth.requestMatchers(HttpMethod.POST, "/sessions").permitAll();
            auth.requestMatchers(HttpMethod.POST, "/access-tokens").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/messages").authenticated();
            auth.requestMatchers("/chat-socket").authenticated();
            auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
            auth.anyRequest().denyAll();
        });
        http.csrf(AbstractHttpConfigurer::disable);
        http.oauth2ResourceServer(oauth2 -> {
            oauth2.jwt(Customizer.withDefaults());
        });
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder,
                                                       DatabaseUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecretKey secretKey(@Value("${auth.jwt.secret-base64}") String secretBase64) {
        byte[] decode = Base64.getDecoder().decode(secretBase64);
        if (decode.length < 32) {
            throw new IllegalStateException("JWT secret must contain at least 32 bytes");
        }
        return new SecretKeySpec(decode, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
