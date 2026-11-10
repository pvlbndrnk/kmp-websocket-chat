package com.pvlbndrnk.messenger.auth.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username
) {
}
