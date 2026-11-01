package com.pvlbndrnk.messenger.auth;

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
    public AuthUserResponse createUser(@Valid @RequestBody RegisterRequest request) {
        User user = authService.createUser(request.username(), request.password());
        return new AuthUserResponse(user.getId(), user.getUsername());
    }
}
