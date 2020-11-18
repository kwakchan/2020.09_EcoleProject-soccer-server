package com.ksu.soccerserver.application;

import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationTeamRepository extends JpaRepository<ApplicationTeam, Long> {
    List<ApplicationTeam> findByApplicationHomeTeam(Team applicationHomeTeam);
    List<ApplicationTeam> findByApplicationAwayTeam(Team applicationAwayTeam);
}
