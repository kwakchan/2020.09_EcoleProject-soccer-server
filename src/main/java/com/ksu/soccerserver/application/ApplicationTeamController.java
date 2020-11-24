package com.ksu.soccerserver.application;


import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.application.dto.ApplicationAwayTeamRequest;
import com.ksu.soccerserver.application.dto.ApplicationHomeTeamRequest;
import com.ksu.soccerserver.application.dto.ApplicationTeamModifyRequest;
import com.ksu.soccerserver.application.dto.ApplicationTeamResponse;
import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.application.enums.HomeStatus;
import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.match.MatchRepository;
import com.ksu.soccerserver.match.dto.MatchRequest;
import com.ksu.soccerserver.match.dto.MatchResponse;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications/teams")
public class ApplicationTeamController {

    private final ApplicationTeamRepository applicationTeamRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;

    // 자신의 팀에서 신청한 경기에 대해 리스트를 보는 api
    @GetMapping("/away/{teamId}")
    public ResponseEntity<?> loadApplicationAways(@PathVariable Long teamId,
                                                 @CurrentAccount Account nowAccount){
        Team awayTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(awayTeam.getOwner().getId().equals(nowAccount.getId())) {

            List<ApplicationTeam> applies = applicationTeamRepository.findByAwayTeam(awayTeam);

            return new ResponseEntity<>(applies, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 자신의 팀에서 요청받은 경기에 대해 리스트를 보는 api
    @GetMapping("/home/{teamId}")
    public ResponseEntity<?> loadApplicationHomes(@PathVariable Long teamId,
                                                  @CurrentAccount Account nowAccount){
        Team homeTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(homeTeam.getOwner().getId().equals(nowAccount.getId())){
            List<ApplicationTeam> applies = applicationTeamRepository.findByHomeTeam(homeTeam);

            return new ResponseEntity<>(applies, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }


    //특정 경기방 상세 정보 보기
    @GetMapping("/{applicationId}")
    public ResponseEntity<?> loadApplication(@PathVariable Long applicationId) {
        ApplicationTeam application = applicationTeamRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 방입니다."));

        ApplicationTeamResponse response = modelMapper.map(application, ApplicationTeamResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //경기방 개설
    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody ApplicationHomeTeamRequest applicationHomeTeamRequest,
                                         @CurrentAccount Account nowAccount){

        Team homeTeam = teamRepository.findById(applicationHomeTeamRequest.getHomeTeamId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(homeTeam.getOwner().getId().equals(nowAccount.getId())) {

            ApplicationTeam createMatch = applicationHomeTeamRequest.toEntity(homeTeam);
            ApplicationTeam savedMatch = applicationTeamRepository.save(createMatch);

            ApplicationTeamResponse response = modelMapper.map(savedMatch, ApplicationTeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else{
            return new ResponseEntity<>("해당 유저는 주장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //개설한 경기방 수정하기
    @PutMapping("/{applicationTeamId}")
    public ResponseEntity<?> modifyMatch(@PathVariable Long applicationTeamId,
                                         @CurrentAccount Account nowAccount,
                                         @RequestBody ApplicationTeamModifyRequest applicationTeamModifyRequest){

        ApplicationTeam apply = applicationTeamRepository.findById(applicationTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 경기방입니다."));

        if(apply.getHomeTeam().getOwner().getId().equals(nowAccount.getId())){
            apply.modifyHomeInfo(applicationTeamModifyRequest);

            ApplicationTeam modifyApply = applicationTeamRepository.save(apply);

            ApplicationTeamResponse response = modelMapper.map(modifyApply, ApplicationTeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }


    // awayTeam -> homeTeam 경기신청
    @PutMapping("/apply/{applicationTeamId}")
    public ResponseEntity<?> applyTeam(@PathVariable Long applicationTeamId,
                                       @RequestBody ApplicationAwayTeamRequest applicationAwayTeamRequest,
                                       @CurrentAccount Account nowAccount){

        ApplicationTeam findMatch = applicationTeamRepository.findById(applicationTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 경기방입니다."));

        Team awayTeam = teamRepository.findById(applicationAwayTeamRequest.getAwayTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(awayTeam.getOwner().getId().equals(nowAccount.getId())){
            findMatch.applyAwayTeam(awayTeam);

            ApplicationTeam appliedMatch = applicationTeamRepository.save(findMatch);
            ApplicationTeamResponse response = modelMapper.map(appliedMatch, ApplicationTeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else{
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

    }

    // HomeTeam에서의 수락 / 거절
    @PutMapping("/{applicationId}/home")
    public ResponseEntity<?> modifyHomeStatus(@PathVariable Long applicationId, @RequestParam String status,
                                               @CurrentAccount Account nowAccount){
        ApplicationTeam apply = applicationTeamRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(apply.getHomeTeam().getOwner().getId().equals(nowAccount.getId())){
            apply.updateHomeStatus(HomeStatus.valueOf(status));

            if(apply.getAwayStatus().name().equals(AwayStatus.PENDING.name()) &&
                    apply.getHomeStatus().name().equals(HomeStatus.ACCEPT.name())){

                Team homeTeam = apply.getHomeTeam();
                Team awayTeam = apply.getAwayTeam();

                // MatchRequest를 통하여 Match를 생성하고 repo에 저장한다.
                Match createMatch = MatchRequest.toEntity(apply, homeTeam, awayTeam);
                Match saveMatch = matchRepository.save(createMatch);
                MatchResponse response = modelMapper.map(saveMatch, MatchResponse.class);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

        } else{
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        applicationTeamRepository.save(apply);

        return new ResponseEntity<>(apply, HttpStatus.OK);
    }

    //AwayTeam에서의 신청 취소
    @PutMapping("/{applicationId}/away")
    public ResponseEntity<?> modifyAwayStatus(@PathVariable Long applicationId, @CurrentAccount Account nowAccount){
        ApplicationTeam apply = applicationTeamRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(apply.getAwayTeam().getOwner().getId().equals(nowAccount.getId())) {
            apply.updateAwayStatus(AwayStatus.CANCEL);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        ApplicationTeam updateApply = applicationTeamRepository.save(apply);

        ApplicationTeamResponse response = modelMapper.map(updateApply, ApplicationTeamResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
