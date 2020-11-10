package com.ksu.soccerserver.account;


import com.ksu.soccerserver.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.webresources.JarResourceRoot;
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
    public ResponseEntity<?> join(@RequestBody Map<String, String> account) {
         Account joinAccount = accountRepository.save(Account.builder()
                                    .email(account.get("email"))
                                    .password(passwordEncoder.encode(account.get("password")))
                                    .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                                    .name(account.get("name"))
                                    //Column 추가사항 생기면 추가해주세요
                                    .build());

        return new ResponseEntity<>("Create Account " + joinAccount.toString(), HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> account) {
        Account member = accountRepository.findByEmail(account.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(account.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
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
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        accountRepository.delete(findAccount);

        return new ResponseEntity<>("회원탈퇴 완료", HttpStatus.OK);
    }

    // 팀 탈퇴
    @PutMapping("/{accountId}/withdrawal/{teamId}")
    public ResponseEntity<?> withdrawalTeam(@PathVariable Long accountId, @PathVariable Long teamId) {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        findTeam.getAccounts().remove(findAccount);

        accountRepository.save(findAccount);

        return new ResponseEntity<>("Success Team withdrawal", HttpStatus.OK);
    }
}
