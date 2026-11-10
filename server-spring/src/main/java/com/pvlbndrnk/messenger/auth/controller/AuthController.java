package com.pvlbndrnk.messenger.auth.controller;

import com.pvlbndrnk.messenger.auth.service.AuthService;
import com.pvlbndrnk.messenger.auth.dto.CreateAccessTokenRequest;
import com.pvlbndrnk.messenger.auth.dto.CreateAccessTokenResponse;
import com.pvlbndrnk.messenger.auth.domain.CreateAccessTokenResult;
import com.pvlbndrnk.messenger.auth.dto.CreateSessionRequest;
import com.pvlbndrnk.messenger.auth.dto.CreateSessionResponse;
import com.pvlbndrnk.messenger.auth.domain.CreateSessionResult;
import com.pvlbndrnk.messenger.auth.dto.CreateUserRequest;
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

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        String username = request.username();
        String password = request.password();
        return createUserResponse(authService.createUser(username, password));
    }

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSessionResponse createSession(@Valid @RequestBody CreateSessionRequest request) {
        String username = request.username();
        String password = request.password();
        CreateSessionResult createSessionResult = authService.createSession(username, password);
        UserResponse userResponse = createUserResponse(createSessionResult.user());
        return new CreateSessionResponse(
                userResponse,
                createSessionResult.accessToken(),
                createSessionResult.refreshToken(),
                createSessionResult.accessTokenExpiresInSeconds()
        );
    }

    @PostMapping("/access-tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccessTokenResponse createAccessToken(@Valid @RequestBody CreateAccessTokenRequest request) {
        String refreshToken = request.refreshToken();
        CreateAccessTokenResult createAccessTokenResult = authService.createAccessToken(refreshToken);
        return new CreateAccessTokenResponse(
                createUserResponse(createAccessTokenResult.user()),
                createAccessTokenResult.accessToken(),
                createAccessTokenResult.accessTokenExpiresInSeconds()
        );
    }

    private UserResponse createUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}
