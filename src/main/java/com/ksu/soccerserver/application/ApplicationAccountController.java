package com.ksu.soccerserver.application;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.application.dto.ApplicationAccountDTO;
import com.ksu.soccerserver.application.dto.ApplicationAccountRequest;
import com.ksu.soccerserver.application.dto.ApplicationAccountResponse;
import com.ksu.soccerserver.application.enums.AccountStatus;
import com.ksu.soccerserver.application.enums.TeamStatus;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/applications/accounts")
@RestController
public class ApplicationAccountController {

    private final ApplicationAccountRepository applicationAccountRepository;
    private final AccountRepository accountRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    // 유저가 자신이 지원한 팀 목록을 가져오는 API
//    @GetMapping
//    public ResponseEntity<?> loadApplicationAccount(@CurrentAccount Account nowAccount){
//
//        List<ApplicationAccountResponse> applicationLists = applicationAccountRepository.findByAccount(nowAccount)
//                .stream().map(applicationAccount -> modelMapper.map(applicationAccount, ApplicationAccountResponse.class))
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<>(applicationLists, HttpStatus.OK);
//    }
    @GetMapping
    public ResponseEntity<?> loadApplicationAccount(@CurrentAccount Account nowAccount){

        List<ApplicationAccountDTO> applicationLists = applicationAccountRepository.findByAccount(nowAccount)
                .stream()
                .map(applicationAccount ->
                                new ApplicationAccountDTO(applicationAccount)
                        )
                .collect(Collectors.toList());

        return new ResponseEntity<>(applicationLists, HttpStatus.OK);
    }

    // 팀장이 자신의 팀에게 지원한 유저들의 목록을 가져오는 API
    @GetMapping("/teams/{ownerId}")
    public ResponseEntity<?> loadApplicationTeam(@PathVariable Long ownerId) {
        Account ownerAccount = accountRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team findTeam = teamRepository.findByOwner(ownerAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저는 팀장이 아닙니다."));

        List<ApplicationAccountDTO> applies = applicationAccountRepository.findByTeam(findTeam)
                .stream()
                .map(applicationAccount ->
                        new ApplicationAccountDTO(applicationAccount)
                    )
                .collect(Collectors.toList());

        return new ResponseEntity<>(applies, HttpStatus.OK);
    }

    // 유저가 팀에 가입신청하는 API
    @PostMapping
    public ResponseEntity<?> applyTeam(@RequestBody ApplicationAccountRequest applicationAccountRequest,
                                       @CurrentAccount Account nowAccount){

        Account findAccount = accountRepository.findById(nowAccount.getId()).
                orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Team findTeam = teamRepository.findById(applicationAccountRequest.getTeamId()).
                orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        // 이미 가입신청한 팀에게는 가입신청을 할 수 없게 하기 위함
        Optional<ApplicationAccount> alreadyExist = applicationAccountRepository.findByAccountAndTeam(findAccount, findTeam);
        if(alreadyExist.isPresent()){
            return new ResponseEntity<>("이미 가입신청한 팀입니다.", HttpStatus.BAD_REQUEST);
        }

        if(findTeam.getOwner().getId().equals(nowAccount.getId())){
            return new ResponseEntity<>("해당 유저는 팀장입니다.", HttpStatus.BAD_REQUEST);
        }

        ApplicationAccount apply = applicationAccountRequest.toEntity(findAccount, findTeam);

        ApplicationAccount applied = applicationAccountRepository.save(apply);

        ApplicationAccountResponse response = modelMapper.map(applied, ApplicationAccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    // Account가 가입취소하는 API
    @PutMapping("/{applicationId}/account")
    public ResponseEntity<?> modifyAccountStatus(@PathVariable Long applicationId,
                                                 @CurrentAccount Account nowAccount){

        ApplicationAccount apply = applicationAccountRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));


        //현재 application에 저장된 유저와 현재 유저가 다른지 판별
        if(apply.getAccount().getId().equals(nowAccount.getId())){
            apply.updateAccountStatus(AccountStatus.CANCEL);
        } else{
            return new ResponseEntity<>("불가능한 요청입니다.",HttpStatus.BAD_REQUEST);
        }

        applicationAccountRepository.save(apply);

        ApplicationAccountResponse response = modelMapper.map(apply, ApplicationAccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // AccountStatus가 PENDING이면서 Team에서 수락을하면 account가 Team에 가입하는 api => ROLE_LEADER
    @PutMapping("/{applicationId}/team")
    public ResponseEntity<?> modifyTeamStatus(@PathVariable Long applicationId, @RequestParam String status,
                                              @CurrentAccount Account nowAccount){

        ApplicationAccount apply = applicationAccountRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        //현재 application에 저장된 팀의 주장의 ID와 현재 접속한 유저의 ID를 판별
        if(apply.getTeam().getOwner().getId().equals(nowAccount.getId())){
            apply.updateTeamStatus(TeamStatus.valueOf(status));

            // 현재 application에 Account의 상태가 PENDING중이면서 Team의 상태가 ACCEPT이면 가입완료
            if (apply.getAccountStatus().name().equals(AccountStatus.PENDING.name())
                    && apply.getTeamStatus().equals(TeamStatus.ACCEPT)){

                Team findTeam = teamRepository.findById(apply.getTeam().getId()).
                        orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

                Account findAccount = accountRepository.findById(apply.getAccount().getId()).
                        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

                findTeam.joinMember(findAccount);
                findAccount.setTeam(findTeam);

                teamRepository.save(findTeam);
                accountRepository.save(findAccount);
            }
            else if(apply.getAccountStatus().equals(AccountStatus.CANCEL)){
                return new ResponseEntity<>("이미 유저가 취소한 가입요청입니다.", HttpStatus.BAD_REQUEST);
            }

        } else{
            return new ResponseEntity<>("불가능한 요청입니다.", HttpStatus.BAD_REQUEST);
        }

        applicationAccountRepository.save(apply);

        ApplicationAccountResponse response = modelMapper.map(apply, ApplicationAccountResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
