package com.ksu.soccerserver.auth;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.auth.dto.LoginRequest;
import com.ksu.soccerserver.auth.dto.LoginResponse;
import com.ksu.soccerserver.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExpiredTokenRepository expiredTokenRepository;
    //DB에서 사용자 인증정보를 가져오는 객체
    //private final UserDetailsService userDetailsService = null; //?
    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Account member = accountRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다.");
        }

        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRoles());

        return new ResponseEntity<> (new LoginResponse(token), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req) {
        String token = req.getHeader("X-AUTH-TOKEN");
        expiredTokenRepository.save(ExpiredToken.builder().token(token).build());
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
