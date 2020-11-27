package com.ksu.soccerserver.match;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByNameContaining(String name);
    List<Match> findAllByStateAndNameContaining(String state, String name);
    List<Match> findAllByStateAndDistrictAndNameContaining(String state, String district, String name);
}
