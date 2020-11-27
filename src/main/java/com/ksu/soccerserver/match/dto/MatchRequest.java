package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.application.enums.HomeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequest {

    private HomeStatus homeStatus;


}
