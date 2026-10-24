package com.pvlbndrnk.messenger.message;

import org.hibernate.annotations.Generated;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @Generated
    private UUID id;

    @Column(nullable = false, length = 4096)
    private String text;

    @Column(nullable = false, length = 32)
    private String username;

    @Generated
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static Message create(String username, String text) {
        Message message = new Message();
        message.username = username;
        message.text = text;
        return message;
    }
}
