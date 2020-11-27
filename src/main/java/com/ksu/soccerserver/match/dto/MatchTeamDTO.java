package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchTeamDTO {
    private String name;
    private String logopath;
    private String description;

    public MatchTeamDTO (Team homeTeam){
        this.name = homeTeam.getName();
        this.logopath = homeTeam.getLogopath();
        this.description = homeTeam.getDescription();

    }

}
