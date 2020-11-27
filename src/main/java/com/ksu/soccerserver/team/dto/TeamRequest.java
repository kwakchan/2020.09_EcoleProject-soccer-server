package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {
    private String name;
    private String state;
    private String district;
    private String description;


    public Team toEntity(Account nowAccount, String image) {
        return Team.builder().name(this.getName())
                .state(this.getState()).district(this.getDistrict())
                .logopath(image)
                .description(this.getDescription())
                .owner(nowAccount).build();
    }
}
