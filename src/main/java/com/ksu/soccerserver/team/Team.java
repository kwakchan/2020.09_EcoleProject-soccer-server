package com.ksu.soccerserver.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.invitation.InvitationAccount;
import com.ksu.soccerserver.invitation.InvitationTeam;
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
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String state;

    @Column
    private String district;

    @Column(length = 200)
    private String description;

    @Column
    private String description;

    // 팀의 주장을 나타내는 관계성
    @JsonIgnore
    @OneToOne
    Account owner;

    // 팀에 소속된 인원들과의 관계성
    @JsonIgnore
    @OneToMany(mappedBy = "team")
    Set<Account> accounts = new HashSet<>();

    // 팀에 가입신청한 유저들의 리스트를 나타내는 관계성
    @JsonIgnore
    @OneToMany(mappedBy = "team")
    Set<ApplicationAccount> applicationAccounts = new HashSet<>();

    // 팀에서 가입요청한 유저들의 리스트를 나타내는 관계성
    @JsonIgnore
    @OneToMany(mappedBy = "team")
    Set<InvitationAccount> invitationAccounts = new HashSet<>();

    // 팀에게 경기요청을 받은 리스트를 나타내는 관계성
    @JsonIgnore
    @OneToMany(mappedBy = "applicationHomeTeam")
    Set<ApplicationTeam> applicationHomeTeams = new HashSet<>();

    // 자신의 팀이 경기신청을 보낸 리스트를 나타내는 관계성
    @JsonIgnore
    @OneToMany(mappedBy = "applicationAwayTeam")
    Set<ApplicationTeam> applicationAwayTeams = new HashSet<>();

    // 팀에게 경기요청을 받은 리스트를 나타내는 관계성성
    @JsonIgnore
    @OneToMany(mappedBy = "invitationHomeTeam")
    Set<InvitationTeam> invitationHomeTeams = new HashSet<>();

    // 팀에게 경기요청을 보낸 리스트를 나타내는 관계성
    @JsonIgnore
    @OneToMany(mappedBy = "invitationAwayTeam")
    Set<InvitationTeam> invitationAwayTeams = new HashSet<>();

    // TODO Modify Team infomation Method

    public void joinMember(Account account) {
        accounts.add(account);
    }
}
