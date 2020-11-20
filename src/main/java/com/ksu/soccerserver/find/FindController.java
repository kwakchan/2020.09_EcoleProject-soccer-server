package com.ksu.soccerserver.find;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/find")
@RequiredArgsConstructor
@RestController
public class FindController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/email")
    public ResponseEntity<?> findEmail(
            @RequestParam(value = "name")String name,
            @RequestParam(value = "phoneNum")String phoneNum){
        Account foundAccount = accountRepository.findByNameAndPhoneNum(name, phoneNum)
                .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND, "가입되지 않은 사용자입니다."));
        return new ResponseEntity<>(foundAccount, HttpStatus.OK);
    }

    @GetMapping("/password")
    public ResponseEntity<?> findPW(
            @RequestParam(value = "email")String email,
            @RequestParam(value = "name")String name,
            @RequestParam(value = "phoneNum")String phoneNum){

        Account foundAccount = accountRepository.findByEmailAndNameAndPhoneNum(email, name, phoneNum)
                .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND, "해당하는 사용자 정보를 찾을 수 없습니다."));

        String tempPW = getRandomStr();

        foundAccount.changePW(passwordEncoder.encode(tempPW));
        accountRepository.save(foundAccount);

        return new ResponseEntity<>(tempPW, HttpStatus.OK);
    }

    private String getRandomStr() {
        char[] tmp = new char[12];
        for(int i=0; i<12; i++) {
            int div = (int) Math.floor( Math.random() * 2 );

            if(div == 0) { // 0이면 숫자로
                tmp[i] = (char) (Math.random() * 10 + '0') ;
            }else { //1이면 알파벳
                tmp[i] = (char) (Math.random() * 26 + 'A') ;
            }
        }
        return new String(tmp);
    }
}
