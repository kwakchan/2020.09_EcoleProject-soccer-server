package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeTeamDTO {
    private Long id;
    private String name;
    private String logopath;

    public HomeTeamDTO(Team homeTeam){
        this.id = homeTeam.getId();
        this.name = homeTeam.getName();
        this.logopath = homeTeam.getLogopath();

    }

}
