package com.ksu.soccerserver.account.dto;

import com.ksu.soccerserver.account.Account;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public Account toEntity(PasswordEncoder passwordEncoder) {
        return Account.builder()
                .email(this.getEmail())
                .password(passwordEncoder.encode(this.getPassword()))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .name(this.getName())
                .phoneNum(this.getPhoneNum())
                .birth(this.getBirth())
                .gender(this.getGender())
                .build();
    }
}
