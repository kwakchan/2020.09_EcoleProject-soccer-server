package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.application.ApplicationAccount;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TeamMemberResponse {
    private Long id;
    private String name;
    private String state;
    private String district;
    private String description;
    Account owner;
    Set<Account> accounts;
    Set<ApplicationAccount> applicationAccounts;
}
