package com.ksu.soccerserver.team.dto;

import com.ksu.soccerserver.account.Account;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
public class FilteredTeamsDTO {
    private List<TeamDTO> filteredTeamsDTO;



}
