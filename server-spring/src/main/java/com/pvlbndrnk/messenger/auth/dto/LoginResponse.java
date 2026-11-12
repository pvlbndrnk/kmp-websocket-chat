package com.pvlbndrnk.messenger.auth.dto;

public record LoginResponse(
        UserResponse user,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInSeconds
) {
}
