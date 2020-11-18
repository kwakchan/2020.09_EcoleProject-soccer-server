package com.ksu.soccerserver.invitation;

import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationTeamRepository extends JpaRepository<InvitationTeam, Long> {
    List<InvitationTeam> findByInvitationHomeTeam(Team invitationHomeTeam);
    List<InvitationTeam> findByInvitationAwayTeam(Team invitationAwayTeam);
}
