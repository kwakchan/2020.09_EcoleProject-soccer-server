package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.enums.HomeStatus;
import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.match.enums.MatchStatus;
import com.ksu.soccerserver.team.dto.TeamDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationTeamMatchDTO {
    private Long id;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private String description;
    private HomeStatus homeStatus;
    private MatchStatus matchStatus;
    private TeamDTO homeTeam;

    public ApplicationTeamMatchDTO(Match match) {
        this.id = match.getId();
        this.date = match.getDate();
        this.state = match.getState();
        this.district = match.getDistrict();
        this.countMember = match.getCountMember();
        this.description = match.getDescription();
        this.homeStatus = match.getHomeStatus();
        this.matchStatus = match.getMatchStatus();
        this.homeTeam = new TeamDTO(match.getHomeTeam());
    }
}
