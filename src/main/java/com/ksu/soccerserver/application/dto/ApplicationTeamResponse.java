package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.application.enums.HomeStatus;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationTeamResponse {
    private Long id;
    private Team homeTeam;
    private Team awayTeam;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private String description;
    private HomeStatus homeStatus;
    private AwayStatus awayStatus;
}
