package com.pvlbndrnk.messenger.auth.repository;

import com.pvlbndrnk.messenger.auth.domain.RefreshSession;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {
    @EntityGraph(attributePaths = "user")
    Optional<RefreshSession> findByTokenHash(String tokenHash);
}
