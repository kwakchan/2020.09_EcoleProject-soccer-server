package com.ksu.soccerserver.apply;


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
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ApplyStatus applyStatus;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Team team;

    public void joinApply(Account account, Team team){
        this.account = account;
        this.team = team;
        this.applyStatus = ApplyStatus.APPLY_PENDING;
    }

    public void updateStatus(ApplyStatus applyStatus){
        this.applyStatus = applyStatus;
    }
}
