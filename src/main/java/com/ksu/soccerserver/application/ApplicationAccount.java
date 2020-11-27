package com.ksu.soccerserver.application;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.application.enums.AccountStatus;
import com.ksu.soccerserver.application.enums.TeamStatus;
import com.ksu.soccerserver.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity @Table
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ApplicationAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private AccountStatus accountStatus;

    @Enumerated(value = EnumType.STRING)
    private TeamStatus teamStatus;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Team team;

    public void updateAccountStatus(AccountStatus accountStatus) {this.accountStatus = accountStatus;}
    public void updateTeamStatus(TeamStatus teamStatus) { this.teamStatus = teamStatus; }

}
