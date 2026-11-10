package com.pvlbndrnk.messenger.auth.domain;

import com.pvlbndrnk.messenger.user.User;

import org.hibernate.annotations.Generated;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "refresh_sessions")
public class RefreshSession {
    @Id
    @Generated
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Generated
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    public static RefreshSession create(User user, String tokenHash, Instant expiresAt) {
        RefreshSession refreshSession = new RefreshSession();
        refreshSession.user = user;
        refreshSession.tokenHash = tokenHash;
        refreshSession.expiresAt = expiresAt;
        return refreshSession;
    }
}
