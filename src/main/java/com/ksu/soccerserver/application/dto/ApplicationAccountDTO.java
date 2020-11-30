package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.dto.AccountTeamDTO;
import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.application.enums.AccountStatus;
import com.ksu.soccerserver.application.enums.TeamStatus;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.dto.TeamDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class ApplicationAccountDTO {
//    private String name;
//    private String image;
//    private Long accountId;
//    private Long teamId;

//    public ApplicationAccountDTO(ApplicationAccount applicationAccount){
//        this.accountId = applicationAccount.getAccount().getId();
//        this.name = applicationAccount.getAccount().getName();
//        this.image = applicationAccount.getAccount().getImage();
//        this.teamId = applicationAccount.getTeam().getId();
//    }

    private Long id;
    Account account;             // 신청 유저 정보
    AccountStatus accountStatus; // 유저 수락 여부
    Team team;                   // 신청 팀 정보
    TeamStatus teamStatus;       // 팀에서 수락 여부

    public ApplicationAccountDTO(ApplicationAccount applicationAccount){
        this.id = applicationAccount.getId();
        this.account = applicationAccount.getAccount();
        this.accountStatus = applicationAccount.getAccountStatus();
        this.team = applicationAccount.getTeam();
        this.teamStatus = applicationAccount.getTeamStatus();
    }

}
