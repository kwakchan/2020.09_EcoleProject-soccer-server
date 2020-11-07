package com.ksu.soccerserver.account;

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
    private Set<Grouping> groups;


    public void setName(String name) {
        this.name = name;
    }

}
