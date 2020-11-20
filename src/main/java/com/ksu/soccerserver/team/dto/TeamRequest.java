package com.ksu.soccerserver.team.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {
    private String name;
    private String state;
    private String district;
    private String description;
    
}
