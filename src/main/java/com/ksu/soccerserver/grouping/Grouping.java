package com.ksu.soccerserver.grouping;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Table
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Grouping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String status;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Team team;


    public void joinApply(Account account, Team team){
        this.account = account;
        this.team = team;
    }

    public void joinRequest(Team team, Account account){
        this.team = team;
        this.account = account;
    }

    public void updateStatus(String status){
        this.status = status;
    }

}
