package com.ksu.soccerserver.apply;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
    List<Apply> findByAccount(Account account);
    List<Apply> findByTeam(Team team);
}
