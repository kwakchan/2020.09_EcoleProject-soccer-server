package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.application.enums.HomeStatus;
import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequest {

    private HomeStatus homeStatus;


}
