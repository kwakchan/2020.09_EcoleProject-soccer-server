package com.ksu.soccerserver.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksu.soccerserver.team.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp()    {
        teamRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("회원 가입 성공 -> 201 CREATED")
    @Test
    void create_account() throws Exception  {
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

        // DB에서 account 불러오기
        Account savedAccount = accountRepository.findByEmail(account.getEmail()).get();

        // 값 확인
        assertEquals(savedAccount.getEmail(), account.getEmail());
        assertTrue(passwordEncoder.matches(account.getPassword(), savedAccount.getPassword()));
        assertTrue(savedAccount.getRoles().contains("ROLE_USER"));
        assertEquals(savedAccount.getName(), account.getName());

    }

    // TODO 회원 가입 실패 -> 중복된 이메일 -> 400 BAD_REQUEST

    @DisplayName("회원 정보 불러오기 -> 200 OK")
    @Test
    @Disabled
    void load_account() throws Exception    {
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

        // DB에서 유저 찾기
        Account savedAccount = accountRepository.findByEmail(account.getEmail()).get();

        // 로그인
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // 유저 불러오기
        mockMvc.perform(get("/api/accounts/" + savedAccount.getId())
                .header("X-AUTH-TOKEN", jwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("DB에 없는 유저 불러오기 -> 404 NOT_FOUND")
    @Test
    void load_account_fail_not_exist() throws Exception {
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
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // 유저 아이디가 -1인 유저 불러오기
        mockMvc.perform(get("/api/accounts/-1")
                .header("X-AUTH-TOKEN", jwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("인증이 안된 유저가 특정 유저를 불러올 때 -> 401 UNAUTHORIZED")
    @Test
    void load_account_fail_unauthorized() throws Exception  {
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

        // DB에서 유저 찾기
        Account savedAccount = accountRepository.findByEmail(account.getEmail()).get();

        // JWT 없이 요청
        mockMvc.perform(get("/api/accounts/" + savedAccount.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("유저가 다른 회원 정보를 불러올 시 -> 400 BAD_REQUEST")
    @Test
    void load_account_id_not_match() throws Exception   {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // account 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // account2 생성
        Account account2 = Account.builder()
                .email("test2@email.com")
                .password("testPassword2")
                .name("testName2")
                .build();

        // account2 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account2)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // 로그인
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // account1이 account2 정보 불러오기
        mockMvc.perform(get("/api/accounts/" + account2.getId())
                .header("X-AUTH-TOKEN", jwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("회원 정보 수정 -> 200 OK")
    @Test
    void modify_account() throws Exception  {
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

        // DB에서 유저 찾기
        Account savedAccount = accountRepository.findByEmail(account.getEmail()).get();

        // 로그인
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // 수정 된 유저 정보
        Account modifiedAccount = Account.builder().name("modifiedName").build();

        // 유저 수정하기
        mockMvc.perform(put("/api/accounts/" + savedAccount.getId())
                .header("X-AUTH-TOKEN", jwt)
                .content(objectMapper.writeValueAsString(modifiedAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        savedAccount = accountRepository.findByEmail(account.getEmail()).get();
        assertEquals(savedAccount.getName(), modifiedAccount.getName());
    }

    @DisplayName("디비에 없는 유저 수정 시 -> 404 NOT_FOUND")
    @Test
    void modify_account_fail_not_exist() throws Exception   {
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
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // 수정 된 유저 정보
        Account modifiedAccount = Account.builder().name("modifiedName").build();

        // 유저 아이디가 -1인 유저 수정
        mockMvc.perform(put("/api/accounts/-1")
                .header("X-AUTH-TOKEN", jwt)
                .content(objectMapper.writeValueAsString(modifiedAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("인증이 안된 유저가 회원 정보 수정 시 -> 401 UNAUTHORIZED")
    @Test
    void modify_account_fail_unauthorized() throws Exception    {
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

        // DB에서 유저 찾기
        Account savedAccount = accountRepository.findByEmail(account.getEmail()).get();

        // 수정 된 유저 정보
        Account modifiedAccount = Account.builder().name("modifiedName").build();

        // JWT 없이 요청
        mockMvc.perform(put("/api/accounts/" + savedAccount.getId())
                .content(objectMapper.writeValueAsString(modifiedAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("유저가 다른 회원 정보를 수정할 시 -> 400 BAD_REQUEST")
    @Test
    void modify_account_id_not_match() throws Exception {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // account 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // account2 생성
        Account account2 = Account.builder()
                .email("test2@email.com")
                .password("testPassword2")
                .name("testName2")
                .build();

        // account2 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account2)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // 로그인
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // 수정 된 유저 정보
        Account modifiedAccount = Account.builder().name("modifiedName").build();

        // account1이 account2의 정보 수정 시도
        mockMvc.perform(put("/api/accounts/" + account2.getId())
                .header("X-AUTH-TOKEN", jwt)
                .content(objectMapper.writeValueAsString(modifiedAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    // TODO PUT /api/accounts/:accountId/join/:teamId => 팀 가입

    // TODO PUT /api/accounts/:accountId/join/:teamId => 팀 가입 실패 -> 디비에 없는 유저 아이디

    // TODO PUT /api/accounts/:accountId/join/:teamId => 팀 가입 실패 -> 디비에 없는 팀 아이디

    // TODO PUT /api/accounts/:accountId/join/:teamId => 팀 가입 실패 -> 다른 유저가 접근

    @DisplayName("유저 삭제 -> 200 OK")
    @Test
    void remove_account() throws Exception  {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // account 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // DB에서 유저 찾기
        Account savedAccount = accountRepository.findByEmail(account.getEmail()).get();

        // 로그인
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // 유저 삭제
        mockMvc.perform(delete("/api/accounts/" + savedAccount.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", jwt))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        // 해당 유저가 없는지 확인
        assertFalse(accountRepository.findByEmail(account.getEmail()).isPresent());
    }

    @DisplayName("디비에 없는 유저 삭제 시 -> 404 NOT_FOUND")
    @Test
    void remove_account_fail_not_exist() throws Exception   {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // account 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // 로그인
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // 유저 아이디가 -1인 유저 삭제
        mockMvc.perform(delete("/api/accounts/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", jwt))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }

    @DisplayName("인증이 안된 유저가 회원 삭제 시 -> 401 UNAUTHORIZED")
    @Test
    void remove_account_unauthorized() throws Exception {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // account 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // DB에서 유저 찾기
        Account savedAccount = accountRepository.findByEmail(account.getEmail()).get();

        // JWT 없이 요청
        mockMvc.perform(delete("/api/accounts/" + savedAccount.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;

    }

    @DisplayName("유저가 다른 회원 삭제 시 -> 400 BAD_REQUEST")
    @Test
    void remove_account_id_not_match() throws Exception {
        // account 생성
        Account account = Account.builder()
                .email("test@email.com")
                .password("testPassword")
                .name("testName")
                .build();

        // account 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // account2 생성
        Account account2 = Account.builder()
                .email("test2@email.com")
                .password("testPassword2")
                .name("testName2")
                .build();

        // account2 회원가입
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account2)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        // 로그인
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print());

        // Response에서 JWT 추출
        String jwt = resultActions.andReturn().getResponse().getContentAsString();

        // account1이 account2 삭제 시도
        mockMvc.perform(delete("/api/accounts/" + account2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    // TODO PUT /api/acounts/:accountId/withdrawal/:teamId => 팀 탈퇴

    // TODO PUT /api/accounts/:accountId/withdrawal/:teamId => 팀 탈퇴 실패 -> 디비에 없는 유저 아이디

    // TODO PUT /api/accounts/:accountId/withdrawal/:teamId => 팀 탈퇴 실패 -> 디비에 없는 팀 아이디

}
