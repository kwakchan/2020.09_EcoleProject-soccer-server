package com.ksu.soccerserver.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String gender;
    private String birth;
}
