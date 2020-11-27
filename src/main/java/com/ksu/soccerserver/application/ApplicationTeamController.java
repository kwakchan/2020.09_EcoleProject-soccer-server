package com.ksu.soccerserver.application;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.application.dto.ApplicationAwayTeamRequest;
import com.ksu.soccerserver.application.dto.ApplicationTeamResponse;
import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.match.Match;
import com.ksu.soccerserver.match.MatchRepository;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications/teams") // => ROLE_LEADER
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

            List<ApplicationTeamResponse> findApplies = applicationTeamRepository.findByApplyTeamsId(awayTeam.getId())
                    .stream().map(applicationTeam -> modelMapper.map(applicationTeam, ApplicationTeamResponse.class))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(findApplies, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // awayTeam -> homeTeam 경기신청
    @PutMapping("/apply/{matchId}")
    public ResponseEntity<?> applyTeam(@PathVariable Long matchId,
                                       @RequestBody ApplicationAwayTeamRequest applicationAwayTeamRequest,
                                       @CurrentAccount Account nowAccount){

        Match findMatch = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 경기방입니다."));

        Team findTeam = teamRepository.findById(applicationAwayTeamRequest.getAwayTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(findTeam.getOwner().getId().equals(nowAccount.getId())){

            ApplicationTeam applyTeam = applicationAwayTeamRequest.toEntity(findMatch,findTeam);

            ApplicationTeam appliedMatch = applicationTeamRepository.save(applyTeam);
            ApplicationTeamResponse response = modelMapper.map(appliedMatch, ApplicationTeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else{
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }


    //AwayTeam에서의 신청 취소
    @PutMapping("/{applicationId}/away")
    public ResponseEntity<?> modifyAwayStatus(@PathVariable Long applicationId, @CurrentAccount Account nowAccount){
        ApplicationTeam apply = applicationTeamRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(apply.getApplyTeams().getOwner().getId().equals(nowAccount.getId())) {
            apply.updateAwayStatus(AwayStatus.CANCEL);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        ApplicationTeam updateApply = applicationTeamRepository.save(apply);

        ApplicationTeamResponse response = modelMapper.map(updateApply, ApplicationTeamResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}