package com.pvlbndrnk.messenger.auth.service;

import com.pvlbndrnk.messenger.user.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AccessTokenService {
    private final Duration accessTtl;
    private final JwtEncoder jwtEncoder;

    public AccessTokenService(@Value("${auth.jwt.access-ttl}") Duration accessTtl, JwtEncoder jwtEncoder) {
        this.accessTtl = accessTtl;
        this.jwtEncoder = jwtEncoder;
    }

    public long accessTokenTtlInSeconds() {
        return accessTtl.toSeconds();
    }

    public String createToken(User user) {
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
}
