package com.ksu.soccerserver.application;


import com.ksu.soccerserver.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Table
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ApplicationTeam {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @ManyToOne
    Team applicationHomeTeam;

    @ManyToOne
    Team applicationAwayTeam;

    public void updateStatus(ApplicationStatus applicationStatus) { this.applicationStatus = applicationStatus; }

}
