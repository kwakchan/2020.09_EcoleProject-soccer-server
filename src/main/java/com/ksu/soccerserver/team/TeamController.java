package com.ksu.soccerserver.team;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.team.dto.TeamModifyRequest;
import com.ksu.soccerserver.team.dto.TeamRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
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
    public ResponseEntity<?> createTeam(@CurrentAccount Account nowAccount, @RequestBody TeamRequest teamRequest){

        Account currentAccount = accountRepository.findByEmail(nowAccount.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team makingTeam = Team.builder().name(teamRequest.getName())
                .state(teamRequest.getState()).district(teamRequest.getDistrict())
                .description(teamRequest.getDescription()).owner(nowAccount).build();

        currentAccount.setLeadingTeam(makingTeam);
        currentAccount.setTeam(makingTeam);

        teamRepository.save(makingTeam);
        accountRepository.save(currentAccount);

        return new ResponseEntity<>(makingTeam, HttpStatus.CREATED);
    }

    // 생성되어 있는 모든 팀 GET
//    @GetMapping
//    public ResponseEntity<?> loadAllFilteredTeams(@RequestParam(required = false) String teamName,
//                                                  @RequestParam String state,
//                                                  @RequestParam String district,
//                                                  Pageable pageable){
//
//        Page<Team> teams = teamRepository.findByTeamName(teamName);
//
//        if (teams.isEmpty()) {
//            return new ResponseEntity<>("생성된 팀이 없습니다.", HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity<>(teams, HttpStatus.OK);
//    }

    // 해당 teamId를 가진 팀 GET
    @GetMapping("/{teamId}")
    public ResponseEntity<?> loadTeam(@PathVariable Long teamId){
        Team findTeam = teamRepository.findById(teamId).orElseThrow
                (() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        return new ResponseEntity<>(findTeam, HttpStatus.OK);
    }

    // 해당 팀의 정보 수정
    @PutMapping("/{teamId}")
    public ResponseEntity<?> putTeam(@PathVariable Long teamId, @CurrentAccount Account nowAccount,
                                     @RequestBody TeamModifyRequest teamModifyRequest) {
        Team findTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다"));

        if(findTeam.getOwner().getId().equals(nowAccount.getId())){
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        } else{
            findTeam.updateTeamInfo(teamModifyRequest);

            teamRepository.save(findTeam);

            return new ResponseEntity<>(findTeam, HttpStatus.OK);
        }
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long teamId, @CurrentAccount Account nowAccount) {
        Team findTeam = teamRepository.findById(teamId).orElseThrow
                (() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));
        if(findTeam.getOwner().getId().equals(nowAccount.getId())){
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }else {
            teamRepository.delete(findTeam);

            return new ResponseEntity<>(findTeam, HttpStatus.OK);
        }
    }
}
