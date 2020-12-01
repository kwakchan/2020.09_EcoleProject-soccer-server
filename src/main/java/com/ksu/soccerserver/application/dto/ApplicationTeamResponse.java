package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.team.dto.TeamDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationTeamResponse {
    private Long id;
    private ApplicationTeamMatchDTO match;    //어느 경기방?
    private TeamDTO applyTeam;  //신청자의 팀  자기 자신
    private AwayStatus awayStatus;

    public ApplicationTeamResponse(ApplicationTeam applicationTeam) {
        this.id = applicationTeam.getId();
        this.match = new ApplicationTeamMatchDTO(applicationTeam.getMatch());
        this.applyTeam = new TeamDTO(applicationTeam.getApplyTeams());
        this.awayStatus = applicationTeam.getAwayStatus();
    }

}
