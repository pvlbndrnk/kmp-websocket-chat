package com.pvlbndrnk.messenger.auth.service;

import com.pvlbndrnk.messenger.auth.exception.InvalidRefreshTokenException;
import com.pvlbndrnk.messenger.auth.domain.RefreshSession;
import com.pvlbndrnk.messenger.auth.repository.RefreshSessionRepository;
import com.pvlbndrnk.messenger.user.User;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshSessionService {
    private final RefreshSessionRepository refreshSessionRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String createSession(User user) {
        String rawToken = generateToken();
        String rawTokenHash = hashToken(rawToken);

        Instant expiresAt = Instant.now().plus(Duration.ofDays(30));

        RefreshSession refreshSession = RefreshSession.create(user, rawTokenHash, expiresAt);
        refreshSessionRepository.save(refreshSession);
        return rawToken;
    }

    public RefreshSession findValidSession(String rawToken) {
        String tokenHash = hashToken(rawToken);
        RefreshSession session = refreshSessionRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);
        Instant now = Instant.now();
        if (session.getRevokedAt() != null || !session.getExpiresAt().isAfter(now)) {
            throw new InvalidRefreshTokenException();
        }
        return session;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            byte[] tokenDigest = MessageDigest.getInstance("SHA-256").digest(tokenBytes);
            return HexFormat.of().formatHex(tokenDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
