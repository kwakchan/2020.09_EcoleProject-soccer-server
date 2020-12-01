package com.ksu.soccerserver.match.dto;

import com.ksu.soccerserver.application.dto.ApplicationTeamResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatchesResponse {
    private List<ApplicationTeamResponse> applicationTeamResponses;

    public MatchesResponse(List<ApplicationTeamResponse> applicationTeamResponses) {
        this.applicationTeamResponses = applicationTeamResponses;
    }
}
