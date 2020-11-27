package com.ksu.soccerserver.team;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.invitation.InvitationAccount;
import com.ksu.soccerserver.invitation.InvitationTeam;
import com.ksu.soccerserver.team.dto.TeamModifyRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity @Table
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String state;

    @Column
    private String logopath;

    private String district;

    @Column(length = 200)
    private String description;

    // 팀의 주장을 나타내는 관계성
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToOne
    private Account owner;

    // 팀에 소속된 인원들과의 관계성
    //@JsonIgnore
    @OneToMany(mappedBy = "team")
    private final Set<Account> accounts = new HashSet<>();

    // 팀에 가입신청한 유저들의 리스트를 나타내는 관계성
    //@JsonIgnore
    @OneToMany(mappedBy = "team")
    private final Set<ApplicationAccount> applicationAccounts = new HashSet<>();

    // 팀에서 가입요청한 유저들의 리스트를 나타내는 관계성
    //@JsonIgnore
    @OneToMany(mappedBy = "team")
    private final Set<InvitationAccount> invitationAccounts = new HashSet<>();

    // 팀에게 경기요청을 받은 리스트를 나타내는 관계성
    //@JsonIgnore
    @OneToMany(mappedBy = "applicationHomeTeam")
    private final Set<ApplicationTeam> applicationHomeTeams = new HashSet<>();

    // 자신의 팀이 경기신청을 보낸 리스트를 나타내는 관계성
    @OneToMany(mappedBy = "applicationAwayTeam")
    private final Set<ApplicationTeam> applicationAwayTeams = new HashSet<>();

    // 팀에게 경기요청을 받은 리스트를 나타내는 관계성성
    @OneToMany(mappedBy = "invitationHomeTeam")
    private final Set<InvitationTeam> invitationHomeTeams = new HashSet<>();

    // 팀에게 경기요청을 보낸 리스트를 나타내는 관계성
    //@JsonIgnore
    @OneToMany(mappedBy = "invitationAwayTeam")
    private final Set<InvitationTeam> invitationAwayTeams = new HashSet<>();

    public void updateTeamInfo(TeamModifyRequest teamModifyRequest){
        this.description = teamModifyRequest.getDescription();
    }

    public void joinMember(Account account) {
        accounts.add(account);
    }
    // Logo 수정
    public void setLogo(String logo) { this.logopath = logo; }


}
