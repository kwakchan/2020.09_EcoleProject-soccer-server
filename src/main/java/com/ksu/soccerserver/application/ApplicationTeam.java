package com.ksu.soccerserver.application;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.match.Match;
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
public class ApplicationTeam {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private AwayStatus awayStatus;

    @ManyToOne
    private Team applyTeams;

    @ManyToOne
    private Match match;

    public void updateAwayStatus(AwayStatus awayStatus){
        this.awayStatus = awayStatus;
    }

}
