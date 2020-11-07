package com.ksu.soccerserver.account;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<?> postAccount(@RequestBody Account account){

        accountRepository.save(account);

        return new ResponseEntity<>("Create Account", HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable Long accountId){
        try {
            Account findAccount = accountRepository.findById(accountId).get();

            return new ResponseEntity<>(findAccount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error!!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> postAccount(@PathVariable Long accountId, @RequestBody Account account) {
        try {
            Account findAccount = accountRepository.findById(accountId).get();

            findAccount.setName(account.getName());
            accountRepository.save(findAccount);

            return new ResponseEntity<>("회원정보 수정완료", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error!!", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId){
        try {
            accountRepository.deleteById(accountId);

            return new ResponseEntity<>("회원탈퇴 완료", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error!!", HttpStatus.BAD_REQUEST);
        }
    }

}
