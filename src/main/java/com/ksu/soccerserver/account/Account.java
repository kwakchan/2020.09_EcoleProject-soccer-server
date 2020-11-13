package com.ksu.soccerserver.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.invitation.InvitationAccount;
import com.ksu.soccerserver.team.Team;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity @Table
@Getter
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Email 길이=100, UNIQUE, Not NULL
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    //Password 길이=400, UNIQUE, Not NULL
    @Column(length = 400, nullable = false)
    private String password;

    @Column
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<ApplicationAccount> apply = new HashSet<>();

    @ManyToOne
    private Team team;

    @OneToOne
    Team leadingTeam;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<InvitationAccount> invitationAccount = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    public void updateMyInfo(String name) { this.name = name; }

    public void joinTeam(Team team) { this.team = team; }

}
