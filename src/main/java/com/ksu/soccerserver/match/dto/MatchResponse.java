package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.application.ApplicationTeam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    private Long id;
    private ApplicationTeam applicationTeam;
    private Long homeTeamId;
    private Long awayTeamId;
}
