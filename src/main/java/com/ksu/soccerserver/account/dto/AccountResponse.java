package com.ksu.soccerserver.account.dto;

import com.ksu.soccerserver.account.Account;
import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {
    private Long id;
    private String email;
    private String name;
    private String phoneNum;
    private String birth;
    private String gender;
    private String image;
    private String position;
    private String state;
    private String district;
    private String weight;
    private String height;
    private String foot;
    private AccountTeamDTO team;
    private AccountTeamDTO leadingTeam;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.name = account.getName();
        this.phoneNum = account.getPhoneNum();
        this.birth = account.getBirth();
        this.gender = account.getGender();
        this.image = account.getImage();
        this.position = account.getPosition();
        this.state = account.getState();
        this.district = account.getDistrict();
        this.weight = account.getWeight();
        this.height = account.getHeight();
        this.foot = account.getFoot();
        if(account.getTeam()!=null){
            this.team = new AccountTeamDTO(account.getTeam());
            this.leadingTeam = new AccountTeamDTO(account.getLeadingTeam());
        }

    }
}
