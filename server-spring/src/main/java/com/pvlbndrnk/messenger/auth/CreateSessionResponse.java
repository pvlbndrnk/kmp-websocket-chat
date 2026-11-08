package com.pvlbndrnk.messenger.auth;

public record CreateSessionResponse(
        UserResponse user,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInSeconds
) {
}
