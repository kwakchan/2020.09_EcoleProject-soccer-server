package com.ksu.soccerserver.match.dto;


import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.match.enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    private Long id;
    private boolean isLeader;
    private HomeTeamDTO homeTeam;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private String description;
    private MatchStatus matchStatus;

    public MatchResponse (Match match){
        this.homeTeam = new HomeTeamDTO(match.getHomeTeam());
        this.id = match.getId();
        this.date = match.getDate();
        this.state = match.getState();
        this.district = match.getDistrict();
        this.countMember = match.getCountMember();
        this.description = match.getDescription();
        this.matchStatus = match.getMatchStatus();
    }
}
