package com.pvlbndrnk.messenger.auth.domain;

import com.pvlbndrnk.messenger.user.User;

public record CreateSessionResult(
        User user,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInSeconds
) {
}
