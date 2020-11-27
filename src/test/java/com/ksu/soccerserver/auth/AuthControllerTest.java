package com.ksu.soccerserver.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp()    {
        accountRepository.deleteAll();
    }

    @DisplayName("로그인 성공 -> 200 OK")
    @Test
    void login_account() throws Exception   {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // 로그인
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
//                .andExpect(status().isOk())
                .andExpect(status().isCreated())
        ;
    }

    @DisplayName("로그인 실패 by email -> 404 NOT_FOUND")
    @Test
    void login_account_fail_by_email() throws Exception    {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // 기존과 다른 email로 변경
        account = Account.builder()
                .email("wrong@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // 로그인
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("로그인 실패 by password -> 400 BAD_REQUEST")
    @Test
    void login_account_fail_by_password() throws Exception  {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // 기존과 다른 password로 변경
        account = Account.builder()
                .email("test@email.com")
                .password("wrongPassword")
                .name("testName")
                .build();

        // 로그인
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

}
