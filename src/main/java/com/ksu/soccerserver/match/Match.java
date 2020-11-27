package com.ksu.soccerserver.match;

import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.application.enums.HomeStatus;
import com.ksu.soccerserver.match.dto.MatchModifyRequest;
import com.ksu.soccerserver.match.enums.MatchStatus;
import com.ksu.soccerserver.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Set;

@Entity @Table(name = "matching")
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private MatchStatus matchStatus;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    @OneToMany(mappedBy = "match")
    private Set<ApplicationTeam> applicationTeams;

    public void modifyHomeInfo(MatchModifyRequest matchModifyRequest){
        this.date = matchModifyRequest.getDate();
        this.state = matchModifyRequest.getState();
        this.district = matchModifyRequest.getDistrict();
        this.countMember = matchModifyRequest.getCountMember();
        this.description = matchModifyRequest.getDescription();
    }

    public void updateHomeStatus(HomeStatus homeStatus){
        this.homeStatus = homeStatus;
    }

    public void successMatch(Team awayTeam) {
        this.awayTeam = awayTeam;
        this.matchStatus = MatchStatus.PROGRESSING;
    }


}
