package com.ksu.soccerserver.invitation;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
public class InvitationTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private InvitationStatus invitationStatus;

    @ManyToOne
    Team invitationHomeTeam;

    @ManyToOne
    Team invitationAwayTeam;

    public void updateStatus(InvitationStatus invitationStatus) { this.invitationStatus = invitationStatus; }

}
