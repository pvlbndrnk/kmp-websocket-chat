package com.pvlbndrnk.messenger.auth;

import jakarta.validation.constraints.NotBlank;

public record CreateAccessTokenRequest(
        @NotBlank String refreshToken
) {
}
