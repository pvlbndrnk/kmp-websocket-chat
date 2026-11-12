package com.pvlbndrnk.messenger.auth.service;

import com.pvlbndrnk.messenger.user.User;

public record TokenResult(
        User user,
        String token,
        long expiresIn
) {
}
