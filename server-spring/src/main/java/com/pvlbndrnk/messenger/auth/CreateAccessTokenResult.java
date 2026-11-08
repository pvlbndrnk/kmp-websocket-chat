package com.pvlbndrnk.messenger.auth;

import com.pvlbndrnk.messenger.user.User;

public record CreateAccessTokenResult(
        User user,
        String accessToken,
        long accessTokenExpiresInSeconds
) {
}
