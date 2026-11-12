package com.pvlbndrnk.messenger.auth.dto;

public record TokenResponse(
        UserResponse user,
        String token,
        long expiresIn
) {
}
