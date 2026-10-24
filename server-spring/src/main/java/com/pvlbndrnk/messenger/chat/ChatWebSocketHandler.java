package com.pvlbndrnk.messenger.chat;

import com.pvlbndrnk.messenger.message.MessageResponse;
import com.pvlbndrnk.messenger.message.MessageService;
import com.pvlbndrnk.messenger.websocket.ChatWebSocketHandshakeInterceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.SessionLimitExceededException;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final int SEND_TIME_LIMIT = 10_000/* ms */;
    private static final int BUFFER_SIZE_LIMIT = 64 * 1024/* bytes */;

    private final JsonMapper jsonMapper;
    private final MessageService messageService;
    private final Map<String, ConcurrentWebSocketSessionDecorator> members = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = readUsernameAttribute(session);
        ConcurrentWebSocketSessionDecorator sessionDecorator =
                new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        if (members.putIfAbsent(username, sessionDecorator) != null) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Username already connected"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        if (payload.isBlank()) {
            return;
        }
        if (payload.codePointCount(0, payload.length()) > 4096) {
            session.close(CloseStatus.TOO_BIG_TO_PROCESS);
            return;
        }
        String usernameAttribute = readUsernameAttribute(session);
        ConcurrentWebSocketSessionDecorator sender = members.get(usernameAttribute);
        if (sender == null || sender.getDelegate() != session || !sender.isOpen()) {
            return;
        }
        MessageResponse messageResponse = messageService.saveMessage(usernameAttribute, payload);
        TextMessage textMessage = new TextMessage(jsonMapper.writeValueAsString(messageResponse));
        for (WebSocketSession member : members.values()) {
            try {
                if (member.isOpen()) {
                    member.sendMessage(textMessage);
                }
            } catch (IOException | SessionLimitExceededException e) {
                log.warn("Failed to send message to {}", member.getId(), e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = readUsernameAttribute(session);
        members.computeIfPresent(username, (String key, ConcurrentWebSocketSessionDecorator decorator) -> {
            return decorator.getDelegate() == session ? null : decorator;
        });
    }

    private String readUsernameAttribute(WebSocketSession session) {
        return (String) session.getAttributes().get(ChatWebSocketHandshakeInterceptor.USERNAME_ATTRIBUTE);
    }
}
