package com.ksu.soccerserver.team;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.team.dto.TeamModifyRequest;
import com.ksu.soccerserver.team.dto.TeamRequest;
import com.ksu.soccerserver.team.dto.TeamResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.util.Optional;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    // 팀 생성
    @PostMapping
    public ResponseEntity<?> createTeam(@CurrentAccount Account nowAccount, HttpServletRequest request,
                                        @RequestBody TeamRequest teamRequest){

        ServletUriComponentsBuilder defaultPath = ServletUriComponentsBuilder.fromCurrentContextPath();
        String requestUri = defaultPath.toUriString() + request.getRequestURI() + "/images/default.jpg";

        Account currentAccount = accountRepository.findByEmail(nowAccount.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));


        Team makingTeam = teamRequest.toEntity(currentAccount, requestUri);
        /* 방장의 팀 여부 검사 && Team 이름 중복 검사 */
        Optional<Team> isMadeTeam = teamRepository.findByName(makingTeam.getName());
        Optional<Team> isAccountJoinedTeam = teamRepository.findByAccounts(currentAccount);
        if(!isMadeTeam.isPresent()) {
            if(!isAccountJoinedTeam.isPresent()) {
                currentAccount.setLeadingTeam(makingTeam);
                currentAccount.setTeam(makingTeam);

                Team madeTeam = teamRepository.save(makingTeam);
                currentAccount.addRoles("ROLE_LEADER");
                accountRepository.save(currentAccount);

                TeamResponse response = modelMapper.map(madeTeam, TeamResponse.class);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>("이미 가입한 팀이 있습니다.", HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return new ResponseEntity<>("이미 존재하는 팀입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 생성되어 있는 모든 팀 GET
//    @GetMapping
//    public ResponseEntity<?> loadAllFilteredTeams(@RequestParam(required = false) String teamName,
//                                                  @RequestParam String state,
//                                                  @RequestParam String district,
//                                                  Pageable pageable){
//
//        Page<Team> teams = teamRepository.findByTeamName(teamName);
//
//        if (teams.isEmpty()) {
//            return new ResponseEntity<>("생성된 팀이 없습니다.", HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity<>(teams, HttpStatus.OK);
//    }

    // 해당 teamId를 가진 팀 GET
    @GetMapping("/{teamId}")
    public ResponseEntity<?> loadTeam(@PathVariable Long teamId){
        Team findTeam = teamRepository.findById(teamId).orElseThrow
                (() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        TeamResponse response = modelMapper.map(findTeam, TeamResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 해당 팀의 정보 수정
    @PutMapping("/{teamId}")
    public ResponseEntity<?> putTeam(@PathVariable Long teamId, @CurrentAccount Account nowAccount,
                                     @RequestBody TeamModifyRequest teamModifyRequest) {
        Team findTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다"));

        if(findTeam.getOwner().getId().equals(nowAccount.getId())){
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        } else{
            findTeam.updateTeamInfo(teamModifyRequest);

            Team updatedTeam = teamRepository.save(findTeam);

            TeamResponse response = modelMapper.map(updatedTeam, TeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long teamId, @CurrentAccount Account nowAccount) {
        Team findTeam = teamRepository.findById(teamId).orElseThrow
                (() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));
        if(!findTeam.getOwner().getId().equals(nowAccount.getId())){
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }else {
            teamRepository.delete(findTeam);

            TeamResponse response = modelMapper.map(findTeam, TeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
