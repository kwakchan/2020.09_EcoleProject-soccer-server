package com.ksu.soccerserver.match.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchModifyRequest {
    private String date;
    private String state;
    private String district;
    private String countMember;
    private String description;
}
