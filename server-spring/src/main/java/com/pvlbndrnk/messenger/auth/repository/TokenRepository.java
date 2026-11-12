package com.pvlbndrnk.messenger.auth.repository;

import com.pvlbndrnk.messenger.auth.domain.Token;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    @EntityGraph(attributePaths = "user")
    Optional<Token> findByTokenHash(String tokenHash);
}
