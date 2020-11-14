package com.ksu.soccerserver.invitation;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.application.ApplicationAccount;
import com.ksu.soccerserver.application.ApplicationStatus;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/invitations/accounts")
@RequiredArgsConstructor
public class InvitationAccountController {

    private final InvitationAccountRepository invitationAccountRepository;
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;

   // TEAM -> USER 가입요청 API
    @PostMapping
    public ResponseEntity<?> inviteAccount(@RequestBody InvitationAccountDto invitationAccountDto) {
        Account findAccount = accountRepository.findById(invitationAccountDto.getAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team findTeam = teamRepository.findById(invitationAccountDto.getTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        InvitationAccount invite = InvitationAccount.builder().team(findTeam).account(findAccount)
                .invitationStatus(InvitationStatus.INVITE_PENDING).build();

        invitationAccountRepository.save(invite);

        return new ResponseEntity<>(invite, HttpStatus.CREATED);
    }

    // 유저가 보는 자신에게 온 초대 목록 API
    @GetMapping("/{accountId}")
    public ResponseEntity<?> loadInvitationAccount(@PathVariable Long accountId){

        Account findAccount = accountRepository.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        List<InvitationAccount> invitationAccounts = invitationAccountRepository.findByAccount(findAccount);

        return new ResponseEntity<>(invitationAccounts, HttpStatus.OK);
    }

    // 팀장이 보는 팀이 초대한 유저 목록 API
    @GetMapping("/teams/{ownerId}")
    public ResponseEntity<?> loadInvitationTeam(@PathVariable Long ownerId){
        Account ownerAccount = accountRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team findTeam = teamRepository.findByOwner(ownerAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저는 팀장이 아닙니다"));

        List<InvitationAccount> invites = invitationAccountRepository.findByTeam(findTeam);

        return new ResponseEntity<>(invites, HttpStatus.OK);
    }

    // 팀이 보낸 초대 취소 및 팀이 받은 요청 수락 및 거절 API
    @PutMapping("/{invitationId}/{status}")
    public ResponseEntity<?> modifyInviteStatus(@PathVariable Long invitationId, @PathVariable String status){
        InvitationAccount invites = invitationAccountRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(InvitationStatus.valueOf(status) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else { invites.updateStatus(InvitationStatus.valueOf(status)); }
        invitationAccountRepository.save(invites);

        return new ResponseEntity<>(invites, HttpStatus.OK);
    }

}
















