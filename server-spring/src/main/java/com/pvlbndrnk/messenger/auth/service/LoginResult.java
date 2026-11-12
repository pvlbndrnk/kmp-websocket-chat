package com.pvlbndrnk.messenger.auth.service;

import com.pvlbndrnk.messenger.user.User;

public record LoginResult(
        User user,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInSeconds
) {
}
