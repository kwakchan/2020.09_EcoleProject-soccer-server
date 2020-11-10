package com.ksu.soccerserver.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;




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

@Builder
@Entity @Table
@Getter
@NoArgsConstructor @AllArgsConstructor
public class Account implements UserDetails{

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
    private Set<Apply> apply = new HashSet<>();

    @ManyToOne
    private Team team;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<Invite> invite = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public void updateMyInfo(String name) { this.name = name; }

    public void joinTeam(Team team) { this.team = team; }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
