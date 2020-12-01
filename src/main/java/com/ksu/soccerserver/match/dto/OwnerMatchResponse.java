package com.ksu.soccerserver.match.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OwnerMatchResponse {
    List<MatchResponse> matches;
    private boolean isOwner = false;

    public OwnerMatchResponse(List<MatchResponse> matches) {
        this.matches = matches;
    }
}
