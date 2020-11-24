package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
/*Team에 들어가는 Account 정보 담는 객체*/
public class TeamsAccountDTO {
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

    public TeamsAccountDTO(Account account){
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
    }
}


