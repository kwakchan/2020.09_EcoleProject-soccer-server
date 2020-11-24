package com.ksu.soccerserver.application.dto;


import com.ksu.soccerserver.application.enums.AwayStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationAwayTeamRequest {
    private Long awayTeamId;
    private AwayStatus awayStatus;
}
