package com.ksu.soccerserver.application.dto;

import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.application.enums.AccountStatus;
import com.ksu.soccerserver.application.enums.TeamStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class ApplicationAccountDTO {
    private Long id;
    private String name;
    private String image;
    private Long accountId;
    private Long teamId;
    private String teamName;
    private String logopath;
    private AccountStatus accountStatus;
    private TeamStatus teamStatus;

    public ApplicationAccountDTO(ApplicationAccount applicationAccount){
        this.id = applicationAccount.getId();
        this.name = applicationAccount.getAccount().getName();
        this.accountId = applicationAccount.getAccount().getId();
        this.name = applicationAccount.getAccount().getName();
        this.image = applicationAccount.getAccount().getImage();
        this.teamName = applicationAccount.getTeam().getName();
        this.teamId = applicationAccount.getTeam().getId();
        this.logopath = applicationAccount.getTeam().getLogopath();
        this.accountStatus = applicationAccount.getAccountStatus();
        this.teamStatus = applicationAccount.getTeamStatus();
    }
    
}
