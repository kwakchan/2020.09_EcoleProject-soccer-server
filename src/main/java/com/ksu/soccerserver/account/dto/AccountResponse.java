package com.ksu.soccerserver.account.dto;


import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {
    private Long id;
    private String email;
    private String name;
    private String phoneNum;
    private String birth;
    private String gender;
    private String image;
    private String position;
    private String state;
    private String district;
    private String weight;
    private String height;
    private String foot;
    Team team;
    Team leadingTeam;
}
