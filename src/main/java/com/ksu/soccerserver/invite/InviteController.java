package com.ksu.soccerserver.invite;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.apply.ApplyStatus;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor
public class InviteController {

    private final InviteRepository inviteRepository;
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;

//   TEAM -> USER 가입요청
    @PostMapping("/teams/{teamId}/{accountId}")
    public ResponseEntity<?> teamInviteUser(@PathVariable Long teamId, @PathVariable Long accountId) {
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Team"));
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Account"));

        Invite invite = Invite.builder().team(findTeam).account(findAccount).inviteStatus(InviteStatus.INVITE_PENDING).build();
        inviteRepository.save(invite);

        return new ResponseEntity<>(findTeam.getName() + "->" + findAccount.getName() + "가입 요청 완료", HttpStatus.CREATED);
    }

    // 해당 유저{teamId}의 요청리스트 GET
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<?> teamInviteList(@PathVariable Long teamId){
        List<Invite> invites =
                inviteRepository.findByTeam
                        (teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Account")));

        return new ResponseEntity<>(invites, HttpStatus.OK);
    }

    //    가입신청삭제 (DELETE)
    @DeleteMapping("/teams/{teamId}/{inviteId}")
    public ResponseEntity<?> deleteApply(@PathVariable Long teamId, @PathVariable Long inviteId) {

        Invite invite = inviteRepository.findById(inviteId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Invite"));
        invite.updateStatus(InviteStatus.INVITE_CANCEL);

        inviteRepository.deleteById(inviteId);

        return new ResponseEntity<>("Success delete", HttpStatus.OK);
    }

    //    자신에게 가입요청한 TEAMLIST 출력
    @GetMapping("/accounts/{accountId}/teams")
    public ResponseEntity<?> teamApplyList(@PathVariable Long accountId) {
        List<Invite> appliesMember =
                inviteRepository.findByAccount
                        (accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Account")));

        return new ResponseEntity<>(appliesMember, HttpStatus.OK);
    }

    //  자신에게 가입요청을 한 팀에 대하여 수락, 거절 등의 이벤트 api 프론트와 상의 후 진행
//    @PutMapping("/accounts/{accountId}/{inviteId}")
//    public ResponseEntity<?> updateApplyStatus(@PathVariable Long accountId, @PathVariable Long inviteId) {
//
//        Invite invite = inviteRepository.findById(inviteId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Invite"));
//
//        invite.updateStatus(InviteStatus.INVITE_ACCEPT); // 가입요청수락
//        invite.updateStatus(InviteStatus.INVITE_REJECT); // 가입신청거절
//
//        return new ResponseEntity<>(invite, HttpStatus.OK);
//    }
}
