package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationHomeTeamRequest {

    private Long homeTeamId;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private String description;


    public ApplicationTeam toEntity(Team homeTeam){
        return ApplicationTeam.builder().
                homeTeam(homeTeam).
                date(this.date).
                state(this.state).
                district(this.district).
                countMember(this.countMember).
                description(this.description).
                build();
    }
}
