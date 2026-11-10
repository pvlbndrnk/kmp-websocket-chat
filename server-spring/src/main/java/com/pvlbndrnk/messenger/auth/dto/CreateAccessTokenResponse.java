package com.pvlbndrnk.messenger.auth.dto;

public record CreateAccessTokenResponse(
        UserResponse user,
        String accessToken,
        long accessTokenExpiresInSeconds
) {
}
