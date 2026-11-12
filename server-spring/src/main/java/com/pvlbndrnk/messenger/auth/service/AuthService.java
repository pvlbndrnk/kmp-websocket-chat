package com.pvlbndrnk.messenger.auth.service;

import com.pvlbndrnk.messenger.auth.domain.Token;
import com.pvlbndrnk.messenger.auth.exception.UserAlreadyExistsException;
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
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public User register(String username, String password) {
        String normalizedUsername = User.normalizeUsername(username);

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String passwordHash = passwordEncoder.encode(password);
        return userRepository.save(User.create(normalizedUsername, passwordHash));
    }

    public LoginResult login(String username, String password) {
        User user = authenticateUser(username, password);
        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = tokenService.createRefreshToken(user);
        long accessTokenExpiresInSeconds = tokenService.accessTokenTtlInSeconds();
        return new LoginResult(user, accessToken, refreshToken, accessTokenExpiresInSeconds);
    }

    public TokenResult createAccessToken(String refreshToken) {
        Token session = tokenService.findValidRefreshToken(refreshToken);
        User user = session.getUser();
        String accessToken = tokenService.createAccessToken(user);
        long accessTokenExpiresInSeconds = tokenService.accessTokenTtlInSeconds();
        return new TokenResult(user, accessToken, accessTokenExpiresInSeconds);
    }

    private User authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password)
        );
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }
}
