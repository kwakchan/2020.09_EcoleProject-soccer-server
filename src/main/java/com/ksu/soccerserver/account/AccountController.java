package com.ksu.soccerserver.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksu.soccerserver.account.dto.*;
import com.ksu.soccerserver.image.ImageService;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;
    private final ImageService imageService;
    private final ObjectMapper objectMapper;

    // 회원가입
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest accountRequest, HttpServletRequest request) {
        Optional<Account> isJoinedAccount = accountRepository.findByEmail(accountRequest.getEmail());

        ServletUriComponentsBuilder defaultPath = ServletUriComponentsBuilder.fromCurrentContextPath();
        String image = defaultPath.toUriString() + request.getRequestURI() + "/images/account_default.jpg";

        if(!isJoinedAccount.isPresent()){
            Account account = accountRequest.toEntity(passwordEncoder, image);
            Account joinAccount = accountRepository.save(account);

            AccountResponse response = new AccountResponse(joinAccount);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 본인 회원정보 출력
    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(@CurrentAccount Account currentAccount){
        Account account = accountRepository.findByEmail(currentAccount.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        if(currentAccount.getId().equals(account.getId())) {

            AccountResponse response = new AccountResponse(account);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 회원정보 출력
    @GetMapping("/{accountId}")
    public ResponseEntity<?> loadAccount(@PathVariable Long accountId){
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        AccountResponse response = new AccountResponse(findAccount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changeNewPW(
                        @RequestBody AccountPasswordRequest accountPasswordRequest,
                        @CurrentAccount Account currentAccount) {

        Account changingAccount = accountRepository.findById(currentAccount.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if(currentAccount.getId().equals(changingAccount.getId())) {
            if (passwordEncoder.matches(accountPasswordRequest.getOldPW(), changingAccount.getPassword())) {
                changingAccount.changePW(passwordEncoder.encode(accountPasswordRequest.getNewPW()));
                accountRepository.save(changingAccount);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Fail", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 회원정보 수정
    @PutMapping("/{accountId}")
    public ResponseEntity<?> modifyAccount(@PathVariable Long accountId, @RequestPart(value = "image", required = false) MultipartFile image,
                                           @RequestPart("data") String modifyRequest, HttpServletRequest request,
                                           @CurrentAccount Account currentAccount) throws JsonProcessingException {
        Account findAccount = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if(!currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        AccountModifyRequest accountModifyRequest = objectMapper.readValue(modifyRequest, AccountModifyRequest.class);
        String imagePath = imageService.saveImage(image, request);

        findAccount.updateMyInfo(accountModifyRequest, imagePath);
        Account updatedAccount = accountRepository.save(findAccount);
        AccountResponse response = new AccountResponse(updatedAccount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> removeAccount(@PathVariable Long accountId, @CurrentAccount Account currentAccount){
        Account findAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if((currentAccount == null) || !currentAccount.getId().equals(findAccount.getId())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        accountRepository.delete(findAccount);

        AccountResponse response = new AccountResponse(findAccount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팀 탈퇴
    @PutMapping("/withdrawal")
    public ResponseEntity<?> withdrawalTeam(@CurrentAccount Account currentAccount) {

        Account findAccount = accountRepository.findById(currentAccount.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team findTeam = accountRepository.findById(currentAccount.getId()).get().getTeam();

        if(teamRepository.findByAccounts(findAccount).isPresent()){
            findTeam.getAccounts().remove(findAccount);
            findAccount.withdrawTeam();

            Account withdrawalAccount = accountRepository.save(findAccount);
            teamRepository.save(findTeam);

            AccountResponse response = new AccountResponse(withdrawalAccount);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 팀에 회원님이 가입되어 있지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
