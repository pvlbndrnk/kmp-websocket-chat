package com.pvlbndrnk.messenger.user;

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
@Table(name = "users")
public class User {
    @Id
    @Generated
    private UUID id;

    @Column(nullable = false, length = 32)
    private String username;

    @Column(name = "password_hash", columnDefinition = "TEXT", nullable = false)
    private String passwordHash;

    @Generated
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static User create(String username, String passwordHash) {
        User user = new User();
        user.username = username;
        user.passwordHash = passwordHash;
        return user;
    }
}
