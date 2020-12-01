package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.application.dto.ApplicationAccountDTO;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
/* Team 정보 출력을 위한 객체 */
public class TeamDTO {
    private Long id;
    private String name;
    private String state;
    private String logopath;
    private String district;
    private String description;
    private Boolean isOwner = false;
    private TeamsAccountDTO owner;
    private TeamsAccountsDTO accounts;
    private List<ApplicationAccountDTO> applies;

    public TeamDTO() {}

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.state = team.getState();
        this.logopath = team.getLogopath();
        this.district = team.getDistrict();
        this.description = team.getDescription();
        this.owner = new TeamsAccountDTO(team.getOwner());
    }
    public TeamDTO(Team team, List<Account> accounts){
        this.id = team.getId();
        this.name = team.getName();
        this.state = team.getState();
        this.logopath = team.getLogopath();
        this.district = team.getDistrict();
        this.description = team.getDescription();
        this.owner = new TeamsAccountDTO(team.getOwner());
        this.accounts = new TeamsAccountsDTO(accounts);
    }
    public TeamDTO(Team team, List<Account> accounts, List<ApplicationAccountDTO> applies){
        this.id = team.getId();
        this.name = team.getName();
        this.state = team.getState();
        this.logopath = team.getLogopath();
        this.district = team.getDistrict();
        this.description = team.getDescription();
        this.owner = new TeamsAccountDTO(team.getOwner());
        this.accounts = new TeamsAccountsDTO(accounts);
        this.isOwner = true;
        this.applies = applies;
    }
}
