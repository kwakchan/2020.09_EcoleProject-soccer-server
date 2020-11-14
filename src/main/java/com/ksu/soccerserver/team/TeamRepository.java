package com.ksu.soccerserver.team;

import com.ksu.soccerserver.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByOwner(Account account);
}
