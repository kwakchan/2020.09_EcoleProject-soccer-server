package com.ksu.soccerserver.match;

import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Table
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private Team homeMatches;

    @ManyToOne
    private Team awayMatches;

    @OneToOne
    private ApplicationTeam applicationTeam;

}
