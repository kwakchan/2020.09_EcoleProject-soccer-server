package com.ksu.soccerserver.account;

import com.ksu.soccerserver.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody Account account) {
         Account joinAccount = accountRepository.save(Account.builder()
                                    .email(account.getEmail())
                                    .password(passwordEncoder.encode(account.getPassword()))
                                    .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                                    .name(account.getName())
                                    //Column 추가사항 생기면 추가해주세요
                                    .build());

        return new ResponseEntity<>(joinAccount, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Account member = accountRepository.findByEmail(account.getEmail())
                .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND, "가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(account.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다.");
        }

        return new ResponseEntity<> (jwtTokenProvider.createToken(member.getEmail(), member.getRoles()), HttpStatus.OK);
    }



    // 회원정보 출력
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable Long accountId, @CurrentAccount Account currentAccount){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if(!currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(findAccount, HttpStatus.OK);
    }

    // 회원정보 수정
    @PutMapping("/{accountId}")
    public ResponseEntity<?> postAccount(@PathVariable Long accountId, @RequestBody Account account, @CurrentAccount Account currentAccount) {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if(!currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        findAccount.updateMyInfo(account.getName());
        accountRepository.save(findAccount);

        return new ResponseEntity<>(findAccount, HttpStatus.OK);
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

        return new ResponseEntity<>(findAccount, HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId, @CurrentAccount Account currentAccount){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if(!currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        accountRepository.delete(findAccount);

        return new ResponseEntity<>(findAccount, HttpStatus.OK);
    }

    // 팀 탈퇴
    @PutMapping("/{accountId}/withdrawal/{teamId}")
    public ResponseEntity<?> withdrawalTeam(@PathVariable Long accountId, @PathVariable Long teamId) {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        findTeam.getAccounts().remove(findAccount);

        accountRepository.save(findAccount);

        return new ResponseEntity<>(findAccount, HttpStatus.OK);
    }
}
