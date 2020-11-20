package com.ksu.soccerserver.find.dto;

import lombok.Getter;

@Getter
public class FindEmailResponse {
    private String email;

    public FindEmailResponse(String email) {
        this.email = email;
    }
}
