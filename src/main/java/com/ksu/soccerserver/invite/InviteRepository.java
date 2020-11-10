package com.ksu.soccerserver.invite;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    List<Invite> findByAccount(Account account);
    List<Invite> findByTeam(Team team);
}
