package com.ksu.soccerserver.invitation;

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
public class InvitationAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private InvitationStatus invitationStatus;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Team team;

    public void updateStatus(InvitationStatus invitationStatus)
    {
        this.invitationStatus = invitationStatus;
    }
}
