package com.ksu.soccerserver.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ExpiredTokenRepository extends JpaRepository<ExpiredToken, Long> {
    Optional<ExpiredToken> findByToken(String token);
}
