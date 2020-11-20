package com.ksu.soccerserver.application;

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
public class ApplicationAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Team team;

    public void updateStatus(ApplicationStatus applicationStatus) {this.applicationStatus = applicationStatus;}

}
