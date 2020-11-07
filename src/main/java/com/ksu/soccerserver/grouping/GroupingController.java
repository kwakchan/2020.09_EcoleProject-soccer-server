package com.ksu.soccerserver.grouping;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupingController {

    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;
    private final GroupingRepository groupingRepository;

    //USER -> TEAM (APPLY)
    @PostMapping("/api/accounts/{accountId}/apply/{teamId}")
    public ResponseEntity<?> joinRequest(@PathVariable Long accountId, @PathVariable Long teamId){
        try {
            Account findAccount = accountRepository.findById(accountId).get();
            Team findTeam = teamRepository.findById(teamId).get();

            Grouping group = groupingRepository.save(Grouping.builder().build());
            group.joinApply(findAccount, findTeam);

            groupingRepository.save(group);

            return new ResponseEntity<>(findAccount.getName()+" -> "+findTeam.getName()+"팀 신청완료!", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/api/accounts/{accountId}/apply")
    public ResponseEntity<?> requestTeamList(@PathVariable Long accountId){
        try {
            List<Grouping> groups = groupingRepository.findByAccount(accountRepository.findById(accountId).get());
            return new ResponseEntity<>(groups, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("AccoutId Error", HttpStatus.BAD_REQUEST);
        }

    }



    // TEAM -> USER (REQUEST)
    @PostMapping("/api/teams/{teamId}/offer/{accountId}")
    public ResponseEntity<?> offerRequest(@PathVariable Long teamId, @PathVariable Long accountId) {
        try {
            Team findTeam = teamRepository.findById(teamId).get();
            Account findAccount = accountRepository.findById(accountId).get();

            Grouping group = groupingRepository.save(Grouping.builder().build());
            group.joinRequest(findTeam, findAccount);
            groupingRepository.save(group);

            return new ResponseEntity<>(findTeam.getName()+" -> "+findAccount.getName()+" 요청완료!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/api/teams/{teamId}/offer")
    public ResponseEntity<?> offerMemberList(@PathVariable Long teamId){
        try {
            List<Grouping> groups = groupingRepository.findByTeam(teamRepository.findById(teamId).get());
            return new ResponseEntity<>(groups, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("TeamId Error", HttpStatus.BAD_REQUEST);
        }
    }

}
