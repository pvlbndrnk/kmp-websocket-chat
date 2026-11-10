package com.pvlbndrnk.messenger.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAccessTokenRequest(
        @NotBlank String refreshToken
) {
}
