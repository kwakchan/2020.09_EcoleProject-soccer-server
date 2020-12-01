package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.enums.TeamStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationAccountTeamRequest {
    private TeamStatus teamStatus;
}
