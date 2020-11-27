package com.ksu.soccerserver.find;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.find.dto.FindEmailResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RequestMapping("/api/find")
@RequiredArgsConstructor
@RestController
public class FindController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @GetMapping("/email")
    public ResponseEntity<?> findEmail(
            @RequestParam(value = "name")String name,
            @RequestParam(value = "phoneNum")String phoneNum){
        Account foundAccount = accountRepository.findByNameAndPhoneNum(name, phoneNum)
                .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND, "가입되지 않은 사용자입니다."));

        String email = foundAccount.getEmail();

        return new ResponseEntity<>(new FindEmailResponse(email), HttpStatus.OK);
    }

    @GetMapping("/password")
    public ResponseEntity<?> findPW(
            @RequestParam(value = "email")String email,
            @RequestParam(value = "name")String name,
            @RequestParam(value = "phoneNum")String phoneNum){

        Account foundAccount = accountRepository.findByEmailAndNameAndPhoneNum(email, name, phoneNum)
                .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND, "해당하는 사용자 정보를 찾을 수 없습니다."));

        String tempPW = UUID.randomUUID().toString().substring(0,6);;


        foundAccount.changePW(passwordEncoder.encode(tempPW));
        accountRepository.save(foundAccount);


        return new ResponseEntity<>(tempPW, HttpStatus.OK);
    }

}
