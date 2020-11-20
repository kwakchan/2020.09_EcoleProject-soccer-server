package com.ksu.soccerserver.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountPasswordRequest {
    private String oldPW;
    private String newPW;
}
