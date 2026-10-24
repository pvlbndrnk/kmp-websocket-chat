package com.pvlbndrnk.messenger.websocket;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class ChatWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String USERNAME_ATTRIBUTE = "username";

    private static final String USERNAME_REQUEST_PARAMETER = "username";
    private static final String USERNAME_DEFAULT = "Guest";


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String username = null;
        if (request instanceof ServletServerHttpRequest servletRequest) {
            username = servletRequest.getServletRequest().getParameter(USERNAME_REQUEST_PARAMETER);
        }
        if (username == null) {
            username = USERNAME_DEFAULT;
        }
        if (username.isBlank() || username.codePointCount(0, username.length()) > 32) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        attributes.put(USERNAME_ATTRIBUTE, username);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
