package com.ksu.soccerserver.application;

import com.ksu.soccerserver.application.dto.ApplicationTeamResponse;
import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationTeamRepository extends JpaRepository<ApplicationTeam, Long> {
    List<ApplicationTeam> findByApplyTeamsId(Long applyTeamsId);
    List<ApplicationTeam> findByMatchId(Long matchId);
}
