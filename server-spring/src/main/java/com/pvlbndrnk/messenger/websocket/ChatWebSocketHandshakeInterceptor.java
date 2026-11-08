package com.pvlbndrnk.messenger.websocket;

import com.pvlbndrnk.messenger.user.User;
import com.pvlbndrnk.messenger.user.UserRepository;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String USERNAME_ATTRIBUTE = "username";

    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Principal principal = request.getPrincipal();
        if (principal == null) {
            return unauthorized(response);
        }

        UUID userId;
        try {
            userId = UUID.fromString(principal.getName());
        } catch (IllegalArgumentException exception) {
            return unauthorized(response);
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return unauthorized(response);
        }

        attributes.put(USERNAME_ATTRIBUTE, user.getUsername());
        return true;
    }

    private static boolean unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
