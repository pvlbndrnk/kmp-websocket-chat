package com.pvlbndrnk.messenger.auth.dto;

public record CreateSessionResponse(
        UserResponse user,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInSeconds
) {
}
