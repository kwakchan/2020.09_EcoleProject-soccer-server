package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.application.enums.AccountStatus;
import com.ksu.soccerserver.application.enums.TeamStatus;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationAccountResponse {
    private Long id;
    Account account;
    AccountStatus accountStatus;
    Team team;
    TeamStatus teamStatus;
}
