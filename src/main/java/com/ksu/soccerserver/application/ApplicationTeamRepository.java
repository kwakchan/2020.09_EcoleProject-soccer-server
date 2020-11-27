package com.ksu.soccerserver.application;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationTeamRepository extends JpaRepository<ApplicationTeam, Long> {
    List<ApplicationTeam> findByApplyTeamsId(Long applyTeamsId);
    List<ApplicationTeam> findByMatchId(Long matchId);
}
