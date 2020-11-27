package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
/*Team에 가입된 팀원 List 담는 객체*/
public class TeamsAccountsDTO {
    private List<TeamsAccountDTO> teamsAccountsDTOS = new ArrayList<TeamsAccountDTO>();

    public TeamsAccountsDTO(List<Account> accounts) {

        for(int i=0; i<accounts.size(); i++) {
            this.teamsAccountsDTOS.add(new TeamsAccountDTO(accounts.get(i)));
        }
    }

}


