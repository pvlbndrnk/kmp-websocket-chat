package com.pvlbndrnk.messenger.auth;

import com.pvlbndrnk.messenger.user.User;
import com.pvlbndrnk.messenger.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username, String password) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        String passwordHash = passwordEncoder.encode(password);
        return userRepository.save(User.create(normalizedUsername, passwordHash));
    }
}
