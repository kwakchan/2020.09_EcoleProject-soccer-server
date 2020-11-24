package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequest {

    private Long homeTeamId;
    private Long awayTeamId;
    private String date;

    public static Match toEntity(ApplicationTeam applicationTeam, Team homeTeam, Team awayTeam){
        return Match.builder()
                .applicationTeam(applicationTeam)
                .homeMatches(homeTeam)
                .awayMatches(awayTeam)
                .build();
    }
}
