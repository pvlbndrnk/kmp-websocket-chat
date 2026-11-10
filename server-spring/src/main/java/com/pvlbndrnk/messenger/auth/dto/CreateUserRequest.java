package com.pvlbndrnk.messenger.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 32) String username,
        @NotBlank String password
) {
}
