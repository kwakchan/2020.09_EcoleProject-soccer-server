package com.ksu.soccerserver.account;


import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;

    // 회원가입
    @PostMapping
    public ResponseEntity<?> postAccount(@RequestBody Account account){
        accountRepository.save(account);

        return new ResponseEntity<>("Create Account", HttpStatus.CREATED);
    }

    // 회원정보 출력
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable Long accountId){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        return new ResponseEntity<>(findAccount, HttpStatus.OK);
    }

    // 회원정보 수정
    @PutMapping("/{accountId}")
    public ResponseEntity<?> postAccount(@PathVariable Long accountId, @RequestBody Account account) {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        findAccount.updateMyInfo(account.getName());
        accountRepository.save(findAccount);

        return new ResponseEntity<>("회원정보 수정완료", HttpStatus.OK);
    }

    // 팀 가입
    @PutMapping("/{accountId}/join/{teamId}")
    public ResponseEntity<?> joinTeam(@PathVariable Long accountId, @PathVariable Long teamId){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        findAccount.joinTeam(findTeam);
        findTeam.joinMember(findAccount);

        accountRepository.save(findAccount);
        teamRepository.save(findTeam);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId){
        accountRepository.deleteById(accountId);

        return new ResponseEntity<>("회원탈퇴 완료", HttpStatus.OK);
    }

    // 팀 탈퇴
    @PutMapping("/{accountId}/withdrawal/{teamId}")
    public ResponseEntity<?> withdrawalTeam(@PathVariable Long accountId, @PathVariable Long teamId) {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        findAccount.withdrawalTeam();

        accountRepository.save(findAccount);

        return new ResponseEntity<>("Success Team withdrawal", HttpStatus.OK);
    }
}
