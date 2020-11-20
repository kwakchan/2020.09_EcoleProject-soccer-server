package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TeamResponse {
    private Long id;
    private String name;
    private String state;
    private String district;
    private String description;
    Account owner;
    Set<Account> accounts;
}
