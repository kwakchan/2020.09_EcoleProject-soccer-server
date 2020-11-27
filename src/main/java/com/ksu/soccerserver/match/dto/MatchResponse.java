package com.ksu.soccerserver.match.dto;


import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.match.enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    private Long id;
    private HomeTeamDTO homeTeamDTO;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private MatchStatus matchStatus;

    public MatchResponse (Match match){
        this.homeTeamDTO = new HomeTeamDTO(match.getHomeTeam());
        this.id = match.getId();
        this.date = match.getDate();
        this.state = match.getState();
        this.district = match.getDistrict();
        this.countMember = match.getCountMember();
        this.matchStatus = match.getMatchStatus();
    }
}
