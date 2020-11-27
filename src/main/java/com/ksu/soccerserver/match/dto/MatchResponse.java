package com.ksu.soccerserver.match.dto;


import com.ksu.soccerserver.match.enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    private Long id;
    private String date;
    private String state;
    private String district;
    private String countMember;
    private MatchStatus matchStatus;
}
