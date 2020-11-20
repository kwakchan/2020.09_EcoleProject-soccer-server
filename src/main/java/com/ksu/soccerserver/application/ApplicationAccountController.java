package com.ksu.soccerserver.application;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/applications/accounts")
@RestController
public class ApplicationAccountController {

    private final ApplicationAccountRepository applicationAccountRepository;
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;

    // 유저가 자신이 지원한 팀 목록을 가져오는 API
    @GetMapping("/{accountId}")
    public ResponseEntity<?> loadApplicationAccount(@PathVariable Long accountId){
        Account findAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        List<ApplicationAccount> applicationLists = applicationAccountRepository.findByAccount(findAccount);

        return new ResponseEntity<>(applicationLists, HttpStatus.OK);
    }

    // 팀장이 자신의 팀에게 지원한 유저들의 목록을 가져오는 API
    @GetMapping("/teams/{ownerId}")
    public ResponseEntity<?> loadApplicationTeam(@PathVariable Long ownerId) {
        Account ownerAccount = accountRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team findTeam = teamRepository.findByOwner(ownerAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저는 팀장이 아닙니다."));

        List<ApplicationAccount> applies = applicationAccountRepository.findByTeam(findTeam);

        return new ResponseEntity<>(applies, HttpStatus.OK);
    }

    // 유저가 팀에 가입신청하는 API
    @PostMapping
    public ResponseEntity<?> applyTeam(@RequestBody ApplicationAccountDto applicationAccountDto){
        Account findAccount = accountRepository.findById(applicationAccountDto.getAccountId()).
                orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team findTeam = teamRepository.findById(applicationAccountDto.getTeamId()).
                orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        ApplicationAccount apply = ApplicationAccount.builder().account(findAccount).team(findTeam)
                                    .applicationStatus(ApplicationStatus.APPLY_PENDING).build();

        applicationAccountRepository.save(apply);

        return new ResponseEntity<>(apply, HttpStatus.CREATED);
    }

    // 멤버가 취소하거나 팀에서 수락 또는 거절을 하여 상태를 변하시키는 API
    @PutMapping("/{applicationId}/{status}")
    public ResponseEntity<?> modifyApplyStatusAccount(@PathVariable Long applicationId, @PathVariable String status){
        ApplicationAccount apply = applicationAccountRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(ApplicationStatus.valueOf(status) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else { apply.updateStatus(ApplicationStatus.valueOf(status)); }
        applicationAccountRepository.save(apply);

        return new ResponseEntity<>(apply, HttpStatus.OK);
    }



}
