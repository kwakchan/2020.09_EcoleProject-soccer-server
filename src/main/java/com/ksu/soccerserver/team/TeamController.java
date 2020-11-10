package com.ksu.soccerserver.team;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final AccountRepository accountRepository;

    // 팀 생성
    @PostMapping
    public ResponseEntity<?> postTeam(@RequestBody Team team){
        teamRepository.save(team);

        return new ResponseEntity<>("Create Team", HttpStatus.CREATED);
    }

    // 생성되어 있는 모든 팀 GET
    @GetMapping
    public ResponseEntity<?> getTeams(){
        List<Team> teams = teamRepository.findAll();

        if (teams.isEmpty()) { return new ResponseEntity<>("생성된 팀이 없습니다.", HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    // 해당 teamId를 가진 팀 GET
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeam(@PathVariable Long teamId){
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        return new ResponseEntity<>(findTeam, HttpStatus.OK);
    }

    // 해당 팀의 팀원들을 GET
    @GetMapping("/{teamId}/memberList")
    public ResponseEntity<?> getMembers(@PathVariable Long teamId){
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        Set<Account> members = findTeam.getAccounts();

        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    // 해당 팀의 name, location 수정
    @PutMapping("/{teamId}")
    public ResponseEntity<?> putTeam(@PathVariable Long teamId, @RequestBody Team team) {
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));
        findTeam.updateTeamInfo(team.getName(), team.getLocation());
        teamRepository.save(findTeam);

        return new ResponseEntity<>(findTeam.getName()+"팀 정보 수정 완료", HttpStatus.OK);
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long teamId) {
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        teamRepository.delete(findTeam);

        return new ResponseEntity<>("Team Delete Success", HttpStatus.OK);
    }

}
