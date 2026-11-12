package com.pvlbndrnk.messenger.auth.controller;

import com.pvlbndrnk.messenger.auth.service.AuthService;
import com.pvlbndrnk.messenger.auth.dto.TokenRequest;
import com.pvlbndrnk.messenger.auth.dto.TokenResponse;
import com.pvlbndrnk.messenger.auth.service.TokenResult;
import com.pvlbndrnk.messenger.auth.dto.LoginRequest;
import com.pvlbndrnk.messenger.auth.dto.LoginResponse;
import com.pvlbndrnk.messenger.auth.service.LoginResult;
import com.pvlbndrnk.messenger.auth.dto.UserRequest;
import com.pvlbndrnk.messenger.auth.dto.UserResponse;
import com.pvlbndrnk.messenger.user.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserRequest request) {
        String username = request.username();
        String password = request.password();
        return createUserResponse(authService.register(username, password));
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String username = request.username();
        String password = request.password();
        LoginResult loginResult = authService.login(username, password);
        UserResponse userResponse = createUserResponse(loginResult.user());
        return new LoginResponse(
                userResponse,
                loginResult.accessToken(),
                loginResult.refreshToken(),
                loginResult.accessTokenExpiresInSeconds()
        );
    }

    @PostMapping("/auth/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse refreshAccessToken(@Valid @RequestBody TokenRequest request) {
        String refreshToken = request.refreshToken();
        TokenResult tokenResult = authService.createAccessToken(refreshToken);
        return new TokenResponse(
                createUserResponse(tokenResult.user()),
                tokenResult.token(),
                tokenResult.expiresIn()
        );
    }

    private UserResponse createUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}
