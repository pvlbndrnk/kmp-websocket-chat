package com.pvlbndrnk.messenger.auth.service;

import com.pvlbndrnk.messenger.auth.domain.Token;
import com.pvlbndrnk.messenger.auth.exception.InvalidTokenException;
import com.pvlbndrnk.messenger.auth.repository.TokenRepository;
import com.pvlbndrnk.messenger.user.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class TokenService {
    private final Duration accessTtl;
    private final JwtEncoder jwtEncoder;
    private final TokenRepository tokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenService(
            JwtEncoder jwtEncoder,
            TokenRepository tokenRepository,
            @Value("${auth.jwt.access-ttl}") Duration accessTtl
    ) {
        this.jwtEncoder = jwtEncoder;
        this.tokenRepository = tokenRepository;
        this.accessTtl = accessTtl;
    }

    public long accessTokenTtlInSeconds() {
        return accessTtl.toSeconds();
    }

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(accessTtl))
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet))
                .getTokenValue();
    }

    public String createRefreshToken(User user) {
        String rawToken = generateRefreshToken();
        String rawTokenHash = hashRefreshToken(rawToken);

        Instant expiresAt = Instant.now().plus(Duration.ofDays(30));

        Token token = Token.create(user, rawTokenHash, expiresAt);
        tokenRepository.save(token);
        return rawToken;
    }

    public Token findValidRefreshToken(String rawToken) {
        String tokenHash = hashRefreshToken(rawToken);
        Token session = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);
        Instant now = Instant.now();
        if (session.getRevokedAt() != null || !session.getExpiresAt().isAfter(now)) {
            throw new InvalidTokenException();
        }
        return session;
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashRefreshToken(String token) {
        try {
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            byte[] tokenDigest = MessageDigest.getInstance("SHA-256").digest(tokenBytes);
            return HexFormat.of().formatHex(tokenDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
