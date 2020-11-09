package com.ksu.soccerserver.invite;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.apply.ApplyStatus;
import com.ksu.soccerserver.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Table
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private InviteStatus inviteStatus;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Team team;


    public void updateStatus(InviteStatus inviteStatus){
        this.inviteStatus = inviteStatus;
    }
}
