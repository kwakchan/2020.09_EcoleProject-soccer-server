package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.application.enums.AccountStatus;
import com.ksu.soccerserver.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationAccountRequest {

    private Long accountId;
    private Long teamId;

    public ApplicationAccount toEntity(Account findAccount, Team findTeam){
        return ApplicationAccount.builder()
                .account(findAccount).team(findTeam)
                .accountStatus(AccountStatus.PENDING).build();
    }

}
