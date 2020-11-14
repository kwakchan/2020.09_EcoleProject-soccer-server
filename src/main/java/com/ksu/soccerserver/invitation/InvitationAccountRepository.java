package com.ksu.soccerserver.invitation;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvitationAccountRepository extends JpaRepository<InvitationAccount, Long> {
    List<InvitationAccount> findByAccount(Account account);
    List<InvitationAccount> findByTeam(Team team);
}
