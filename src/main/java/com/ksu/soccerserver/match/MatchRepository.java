package com.ksu.soccerserver.match;

import com.ksu.soccerserver.match.enums.MatchStatus;
import com.ksu.soccerserver.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByMatchStatus(MatchStatus matchStatus);
    List<Match> findAllByHomeTeam(Team homeTeam);

}
