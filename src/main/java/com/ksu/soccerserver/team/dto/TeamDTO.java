package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
/* Team 정보 출력을 위한 객체 */
public class TeamDTO {
    private Long id;
    private String name;
    private String state;
    private String logopath;
    private String district;
    private String description;
    private TeamsAccountDTO owner;
    //private List<TeamsAccountDTO> accounts;
    private TeamsAccountsDTO accounts;
/*
    public void setOwner(Account owner) {
        this.owner.setId(owner.getId());
        this.owner.setBirth(owner.getBirth());
        this.owner.setEmail(owner.getEmail());
        this.owner.setDistrict(owner.getDistrict());
        this.owner.setFoot(owner.getFoot());
        this.owner.setPhoneNum(owner.getPhoneNum());
        this.owner.setGender(owner.getGender());
        this.owner.setName(owner.getName());
        this.owner.setState(owner.getState());
        this.owner.setHeight(owner.getHeight());
        this.owner.setWeight(owner.getWeight());
        this.owner.setPosition(owner.getPosition());
        this.owner.setImage(owner.getImage());
    }
*/
    /*
    public void setAccounts(List<Account> accounts){
        for(int i=0; i<accounts.size(); i++) {
            TeamsAccountDTO tempAccount = new TeamsAccountDTO(accounts.get(i));
            this.accounts.add(tempAccount);
        }
    }

     */
}
