package com.ksu.soccerserver.application;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationAccountRepository extends JpaRepository<ApplicationAccount, Long> {
    Optional<ApplicationAccount> findByAccountAndTeam(Account account, Team team);
    List<ApplicationAccount> findByAccount(Account account);
    List<ApplicationAccount> findByTeam(Team team);
}
