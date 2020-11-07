package com.ksu.soccerserver.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksu.soccerserver.grouping.Grouping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity @Table
@Getter
@NoArgsConstructor @AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String location;

    @JsonIgnore
    @OneToMany(mappedBy = "team")
    private Set<Grouping> groups;

    public void updateTeamInfo(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
