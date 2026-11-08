package com.pvlbndrnk.messenger.auth;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username
) {
}
