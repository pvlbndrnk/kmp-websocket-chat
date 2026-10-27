package com.pvlbndrnk.messenger.message;

public record MessageResponse(
        String id,
        String text,
        String userName,
        long timestamp
) {
    static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId().toString(),
                message.getText(),
                message.getUsername(),
                message.getCreatedAt().toEpochMilli()
        );
    }
}
