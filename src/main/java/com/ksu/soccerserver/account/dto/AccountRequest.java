package com.ksu.soccerserver.account.dto;

import com.ksu.soccerserver.account.Account;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import java.util.Collections;

@Getter
@Setter
public class AccountRequest {
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String gender;
    private String birth;
    private String image;
    private String position;
    private String height;
    private String weight;
    private String foot;
    private String state;
    private String district;

    public Account toEntity(PasswordEncoder passwordEncoder, String image) {
        return Account.builder()
                .email(this.getEmail())
                .password(passwordEncoder.encode(this.getPassword()))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .image(image)
                .name(this.getName())
                .phoneNum(this.getPhoneNum())
                .birth(this.getBirth())
                .gender(this.getGender())
                .position(this.getPosition())
                .height(this.getHeight())
                .weight(this.getWeight())
                .foot(this.getFoot())
                .state(this.getState())
                .district(this.getDistrict())
                .build();
    }
}
