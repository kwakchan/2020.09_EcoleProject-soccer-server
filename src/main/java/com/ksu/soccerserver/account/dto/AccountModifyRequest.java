package com.ksu.soccerserver.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountModifyRequest {
    private String password;
    private String position;
    private String state;
    private String district;
    private String weight;
    private String height;
    private String foot;
}
