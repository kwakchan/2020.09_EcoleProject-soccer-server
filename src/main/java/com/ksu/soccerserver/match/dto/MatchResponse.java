package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.match.enums.MatchStatus;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    private Long id;
    private Team homeTeam;
    private Team awayTeam;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private MatchStatus matchStatus;
}
