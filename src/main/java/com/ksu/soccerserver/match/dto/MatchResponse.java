package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    private Long id;
    private Team homeTeam;
    private String date;
    private String state;
    private String district;
    private String countMember;
}
