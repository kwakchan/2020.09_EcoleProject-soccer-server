package com.ksu.soccerserver.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksu.soccerserver.apply.Apply;
import com.ksu.soccerserver.invite.Invite;
import com.ksu.soccerserver.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity @Table
@Getter
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<Apply> apply = new HashSet<>();

    @ManyToOne
    private Team team;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<Invite> invite = new HashSet<>();

    public void updateMyInfo(String name) { this.name = name; }

    public void joinTeam(Team team) { this.team = team; }
}
