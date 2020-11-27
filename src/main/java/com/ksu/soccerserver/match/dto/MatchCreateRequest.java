package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchCreateRequest {

    private Long homeTeamId;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private String description;

    public Match toEntity(Team homeTeam){
        return Match.builder().
                homeTeam(homeTeam).
                date(this.date).
                state(this.state).
                district(this.district).
                countMember(this.countMember).
                description(this.description).
                build();
    }

}
