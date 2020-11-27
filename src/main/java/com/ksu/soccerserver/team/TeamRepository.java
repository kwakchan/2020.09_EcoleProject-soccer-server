package com.ksu.soccerserver.team;

import com.ksu.soccerserver.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByOwner(Account account);
    Optional<Team> findByName(String name);
    Optional<Team> findByAccounts(Account account);
    List<Team> findAllByNameContaining(String name);
    List<Team> findAllByStateAndNameContaining(String state, String name);
    List<Team> findAllByStateAndDistrictAndNameContaining(String state, String district, String name);
}
