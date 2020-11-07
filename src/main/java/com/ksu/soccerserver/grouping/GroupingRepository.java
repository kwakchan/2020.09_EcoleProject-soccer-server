package com.ksu.soccerserver.grouping;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupingRepository extends JpaRepository<Grouping, Long> {
    List<Grouping> findByAccount(Account account);
    List<Grouping> findByTeam(Team team);
}
