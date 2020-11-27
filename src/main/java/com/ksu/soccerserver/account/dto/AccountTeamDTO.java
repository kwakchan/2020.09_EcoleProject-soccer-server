package com.ksu.soccerserver.account.dto;

import com.ksu.soccerserver.team.Team;
import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class AccountTeamDTO {
    private Long id;
    private String name;
    private String state;
    private String logopath;
    private String district;
    private String description;
    @Nullable
    private String owner;

    public AccountTeamDTO(Team team){
        this.id = team.getId();
        this.name = team.getName();
        this.state = team.getState();
        this.logopath = team.getLogopath();
        this.district = team.getDistrict();
        this.description = team.getDescription();
        this.owner = team.getOwner().getName();
    }
}
