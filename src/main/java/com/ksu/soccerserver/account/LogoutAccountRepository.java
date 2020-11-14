package com.ksu.soccerserver.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogoutAccountRepository extends JpaRepository<LogoutAccount, Long> {
    Optional<LogoutAccount> findByToken(String token);
}
