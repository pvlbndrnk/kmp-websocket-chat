package com.pvlbndrnk.messenger.auth;

public record CreateAccessTokenResponse(
        UserResponse user,
        String accessToken,
        long accessTokenExpiresInSeconds
) {
}
