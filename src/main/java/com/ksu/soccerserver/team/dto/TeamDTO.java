package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
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
    private TeamsAccountDTO owner;
    private TeamsAccountsDTO accounts;

    public TeamDTO() {}

    public TeamDTO(Team team, List<Account> accounts){
        final AccountRepository accountRepository;
        this.id = team.getId();
        this.name = team.getName();
        this.state = team.getState();
        this.logopath = team.getLogopath();
        this.district = team.getDistrict();
        this.description = team.getDescription();
        this.owner = new TeamsAccountDTO(team.getOwner());
        this.accounts = new TeamsAccountsDTO(accounts);
    }
}
