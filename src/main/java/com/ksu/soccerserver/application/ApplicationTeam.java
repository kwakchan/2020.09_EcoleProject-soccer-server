package com.ksu.soccerserver.application;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ksu.soccerserver.application.dto.ApplicationTeamModifyRequest;
import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.application.enums.HomeStatus;
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

    @Column
    private String date;

    @Column
    private String state;

    @Column
    private String district;

    @Column
    private String countMember;

    @Column(length = 200)
    private String description;

    @Enumerated(value = EnumType.STRING)
    private HomeStatus homeStatus;

    @Enumerated(value = EnumType.STRING)
    private AwayStatus awayStatus;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    @OneToOne
    private Match match;

    public void applyAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
        this.awayStatus = AwayStatus.PENDING;
    }

    public void modifyHomeInfo(ApplicationTeamModifyRequest applicationTeamModifyRequest){
        this.date = applicationTeamModifyRequest.getDate();
        this.state = applicationTeamModifyRequest.getState();
        this.district = applicationTeamModifyRequest.getDistrict();
        this.countMember = applicationTeamModifyRequest.getCountMember();
        this.description = applicationTeamModifyRequest.getDescription();
    }

    public void updateHomeStatus(HomeStatus homeStatus){
        this.homeStatus = homeStatus;
    }

    public void updateAwayStatus(AwayStatus awayStatus){
        this.awayStatus = awayStatus;
    }

}
