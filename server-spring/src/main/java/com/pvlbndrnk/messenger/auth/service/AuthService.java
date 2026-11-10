package com.pvlbndrnk.messenger.auth.service;

import com.pvlbndrnk.messenger.auth.domain.CreateAccessTokenResult;
import com.pvlbndrnk.messenger.auth.domain.CreateSessionResult;
import com.pvlbndrnk.messenger.auth.domain.RefreshSession;
import com.pvlbndrnk.messenger.auth.exception.UsernameAlreadyExistsException;
import com.pvlbndrnk.messenger.user.User;
import com.pvlbndrnk.messenger.user.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final RefreshSessionService refreshSessionService;
    private final AuthenticationManager authenticationManager;

    public User createUser(String username, String password) {
        String normalizedUsername = User.normalizeUsername(username);

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        String passwordHash = passwordEncoder.encode(password);
        return userRepository.save(User.create(normalizedUsername, passwordHash));
    }

    public CreateSessionResult createSession(String username, String password) {
        User user = authenticateUser(username, password);
        String accessToken = accessTokenService.createToken(user);
        String refreshToken = refreshSessionService.createSession(user);
        long accessTokenExpiresInSeconds = accessTokenService.accessTokenTtlInSeconds();
        return new CreateSessionResult(user, accessToken, refreshToken, accessTokenExpiresInSeconds);
    }

    public CreateAccessTokenResult createAccessToken(String refreshToken) {
        RefreshSession session = refreshSessionService.findValidSession(refreshToken);
        User user = session.getUser();
        String accessToken = accessTokenService.createToken(user);
        long accessTokenExpiresInSeconds = accessTokenService.accessTokenTtlInSeconds();
        return new CreateAccessTokenResult(user, accessToken, accessTokenExpiresInSeconds);
    }

    private User authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password)
        );
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }
}
