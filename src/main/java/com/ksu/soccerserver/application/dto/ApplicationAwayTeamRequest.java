package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationAwayTeamRequest {
    private Long matchId;
    private Long awayTeamId;
    private AwayStatus awayStatus;

    public ApplicationTeam toEntity(Match match, Team applyTeam){
        return ApplicationTeam.builder()
                .match(match)
                .applyTeams(applyTeam)
                .awayStatus(AwayStatus.PENDING)
                .build();
    }
}
