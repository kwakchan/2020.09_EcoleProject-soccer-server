package com.ksu.soccerserver.team;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;

    @PostMapping
    public ResponseEntity<?> postTeam(@RequestBody Team team){
        teamRepository.save(team);

        return new ResponseEntity<>("Create Team", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getTeams(){
        List<Team> teams = teamRepository.findAll();

        return new ResponseEntity(teams, HttpStatus.OK);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeam(@PathVariable Long teamId){
        try {
            Team findTeam = teamRepository.findById(teamId).get();

            return new ResponseEntity<>(findTeam, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Error!!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<?> putTeam(@PathVariable Long teamId, @RequestBody Team team) {
        try {
            Team findTeam = teamRepository.findById(teamId).get();
            findTeam.updateTeamInfo(team.getName(), team.getLocation());
            teamRepository.save(findTeam);

            return new ResponseEntity<>(findTeam.getName()+"팀 정보 수정 완료", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Error!!", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long teamId) {
        try {
            teamRepository.deleteById(teamId);
            return new ResponseEntity<>("Team Delete Success", HttpStatus.OK);

        } catch (Exception e){
            return new ResponseEntity<>("Error!!", HttpStatus.BAD_REQUEST);
        }
    }

}
