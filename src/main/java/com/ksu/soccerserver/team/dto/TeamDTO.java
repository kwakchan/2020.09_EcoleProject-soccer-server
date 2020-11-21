package com.ksu.soccerserver.team.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private String state;
    private String logopath;
    private String district;
    private String description;

    /*
    @OneToOne
    private Account owner;
    @OneToMany(mappedBy = "team")
    private final Set<Account> accounts = new HashSet<>();
     */
}
