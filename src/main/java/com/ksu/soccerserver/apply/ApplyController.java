package com.ksu.soccerserver.apply;


import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityExistsException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apply")
public class ApplyController {

    private final ApplyRepository applyRepository;
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;

    // UESR -> TEAM (유저가 팀에 가입요청)
    @PostMapping("/accounts/{accountId}/{teamId}")
    public ResponseEntity<?> userApplyTeam(@PathVariable Long accountId, @PathVariable Long teamId){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Account"));
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Team"));

        // 아래 주석과 구현과 똑같이 되는데 뭐가 더 좋은 방법인지 궁금합니다.
        Apply apply = Apply.builder().account(findAccount).team(findTeam).applyStatus(ApplyStatus.APPLY_PENDING).build();
        applyRepository.save(apply);

        return new ResponseEntity<>(findAccount.getName() + "->" + findTeam.getName() + "가입 신청 완료", HttpStatus.CREATED);
    }

    // 해당 유저{accountId}의 신청리스트 GET
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> userApplyList(@PathVariable Long accountId){
        List<Apply> applies =
                applyRepository.findByAccount
                        (accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Account")));

        return new ResponseEntity<>(applies, HttpStatus.OK);
    }

//  가입신청삭제 (DELETE)
    @DeleteMapping("/accounts/{accountId}/{applyId}")
    public ResponseEntity<?> deleteApply(@PathVariable Long accountId, @PathVariable Long applyId) {
        applyRepository.deleteById(applyId);

        return new ResponseEntity<>("Success delete", HttpStatus.OK);
    }

//    TEAM기준으로 자신의 TEAM에 가입신청을 한 USERLIST 출력
    @GetMapping("/teams/{teamId}/accounts")
    public ResponseEntity<?> teamApplyList(@PathVariable Long teamId) {
        List<Apply> appliesMember =
                applyRepository.findByTeam
                        (teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Account")));

        return new ResponseEntity<>(appliesMember, HttpStatus.OK);
    }

}
