package com.pvlbndrnk.messenger.security;

import static org.springframework.security.core.userdetails.User.withUsername;

import com.pvlbndrnk.messenger.user.User;
import com.pvlbndrnk.messenger.user.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = User.normalizeUsername(username);

        User user = userRepository
                .findByUsername(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(Collections.emptyList())
                .build();
    }
}
