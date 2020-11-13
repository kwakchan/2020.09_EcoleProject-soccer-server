package com.ksu.soccerserver.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.invitation.InvitationAccount;
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
    private String location;

    @OneToOne
    Account owner;

    @JsonIgnore
    @OneToMany(mappedBy = "team")
    Set<ApplicationAccount> applies = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "team")
    Set<Account> accounts = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "team")
    Set<InvitationAccount> invitationAccount = new HashSet<>();

    public void updateTeamInfo(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public void joinMember(Account account) {
        accounts.add(account);
    }
}
