package com.ksu.soccerserver.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationTeamModifyRequest {
    private String date;
    private String state;
    private String district;
    private String countMember;
    private String description;

}
