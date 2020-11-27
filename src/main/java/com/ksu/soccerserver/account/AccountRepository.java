package com.ksu.soccerserver.account;

import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByNameAndPhoneNum(String name, String phoneNum);
    Optional<Account> findByEmailAndNameAndPhoneNum(String email, String name, String phoneNum);
    List<Account> findAllByTeam(Team team);
}
