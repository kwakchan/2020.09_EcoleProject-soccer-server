package com.ksu.soccerserver.account;

import com.ksu.soccerserver.account.dto.AccountModifyRequest;
import com.ksu.soccerserver.account.dto.AccountRequest;
import com.ksu.soccerserver.account.dto.AccountResponse;
import com.ksu.soccerserver.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Optional;

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
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    // 회원가입
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest accountRequest) {
        Optional<Account> isJoinedAccount = accountRepository.findByEmail(accountRequest.getEmail());

        if(!isJoinedAccount.isPresent()){
            Account account = accountRequest.toEntity(passwordEncoder);
            Account joinAccount = accountRepository.save(account);

            AccountResponse response = modelMapper.map(joinAccount, AccountResponse.class);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        else {
            Account alreadyJoinedAccount = isJoinedAccount.get();
            return new ResponseEntity<>(alreadyJoinedAccount, HttpStatus.BAD_REQUEST);
        }
    }

    // 회원정보 출력
    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(@CurrentAccount Account currentAccount){
        Account account = accountRepository.findByEmail(currentAccount.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        AccountResponse response = modelMapper.map(account, AccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 회원정보 출력
    @GetMapping("/{accountId}")
    public ResponseEntity<?> loadAccount(@PathVariable Long accountId, @CurrentAccount Account currentAccount){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if(!currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        AccountResponse response = modelMapper.map(findAccount, AccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 회원정보 수정
    @PutMapping("/{accountId}")
    public ResponseEntity<?> modifyAccount(@PathVariable Long accountId, @RequestBody AccountModifyRequest modifyRequest, @CurrentAccount Account currentAccount) {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if(!currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        findAccount.updateMyInfo(modifyRequest);
        Account updatedAccount = accountRepository.save(findAccount);

        AccountResponse response = modelMapper.map(updatedAccount, AccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> removeAccount(@PathVariable Long accountId, @CurrentAccount Account currentAccount){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if((currentAccount == null) || !currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        accountRepository.delete(findAccount);

        AccountResponse response = modelMapper.map(findAccount, AccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팀 탈퇴
    @PutMapping("/{accountId}/withdrawal/{teamId}")
    public ResponseEntity<?> withdrawalTeam(@PathVariable Long accountId, @PathVariable Long teamId) {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        Team findTeam = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        findTeam.getAccounts().remove(findAccount);

        Account withdrawalAccount = accountRepository.save(findAccount);

        AccountResponse response = modelMapper.map(withdrawalAccount, AccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
