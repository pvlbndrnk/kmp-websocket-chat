package com.pvlbndrnk.messenger.auth;

import java.util.UUID;

public record AuthUserResponse(
        UUID id,
        String username
) {
}
