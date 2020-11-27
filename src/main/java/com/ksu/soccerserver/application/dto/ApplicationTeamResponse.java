package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationTeamResponse {
    private Long id;
    private Long matchId;
    private Team awayTeam;
    private AwayStatus awayStatus;
}
