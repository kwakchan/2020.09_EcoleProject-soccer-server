package com.ksu.soccerserver.match;


import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.application.ApplicationTeam;
import com.ksu.soccerserver.application.ApplicationTeamRepository;
import com.ksu.soccerserver.application.dto.ApplicationTeamResponse;
import com.ksu.soccerserver.application.enums.AwayStatus;
import com.ksu.soccerserver.application.enums.HomeStatus;
import com.ksu.soccerserver.match.dto.MatchCreateRequest;
import com.ksu.soccerserver.match.dto.MatchModifyRequest;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchRepository matchRepository;
    private final ApplicationTeamRepository applicationTeamRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    // 개설된 모든 경기방 및 필터링된 경기방 GET
    @GetMapping
    public ResponseEntity<?> loadApplications(@RequestParam(required = false) String teamName,
                                              @RequestParam(required = false) String state,
                                              @RequestParam(required = false) String district){

        List<MatchResponse> matches;

        if(!"".equals(teamName.trim())) {
            matches = matchRepository.findAllByHomeTeamContaining(teamName)
            .stream()
            .map(match -> modelMapper.map(match, MatchResponse.class))
            .collect(Collectors.toList());
        }
        //전부 읽기
        else if(!"".equals(state.trim()))
            matches = matchRepository.findAll()
                    .stream()
                    .map(match -> modelMapper.map(match, MatchResponse.class))
                    .collect(Collectors.toList());
        //(광역시, 시, 도)가 전체라면, 모든 팀을 검색
        else if(state.equals("All")) {
            matches = matchRepository.findAll()
                    .stream()
                    .map(match -> modelMapper.map(match, MatchResponse.class))
                    .collect(Collectors.toList());
        }
        //(광역시, 시, 도)가 선택되었고, (구, 면, 읍)이 전체라면
        else if(district.equals("All")){
            matches = matchRepository.findAllByState(state)
                    .stream()
                    .map(match -> modelMapper.map(match, MatchResponse.class))
                    .collect(Collectors.toList());
        }
        //(광역시, 시, 도)가 선택되었고, (구, 면, 읍)또한 선택 시
        else {
            matches = matchRepository.findAllByStateAndDistrict(state, district)
                    .stream()
                    .map(match -> modelMapper.map(match, MatchResponse.class))
                    .collect(Collectors.toList());
        }

        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    //특정 경기방 상세 정보 보기
    @GetMapping("/{matchId}")
    public ResponseEntity<?> loadApplication(@PathVariable Long matchId) {
        Match application = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 방입니다."));

        MatchResponse response = modelMapper.map(application, MatchResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 자신의 팀에서 요청받은 경기에 대해 리스트를 보는 api
    @GetMapping("{teamId}/home/{matchId}")
    public ResponseEntity<?> loadApplicationHomes(@PathVariable Long teamId,
                                                  @PathVariable Long matchId,
                                                  @CurrentAccount Account nowAccount){
        Team homeTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(homeTeam.getOwner().getId().equals(nowAccount.getId())){
            List<ApplicationTeam> applies = applicationTeamRepository.findByMatchId(matchId);

            return new ResponseEntity<>(applies, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }


    //경기방 개설
    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody MatchCreateRequest matchCreateRequest,
                                         @CurrentAccount Account nowAccount){

        Team homeTeam = teamRepository.findById(matchCreateRequest.getHomeTeamId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(homeTeam.getOwner().getId().equals(nowAccount.getId())) {

            Match createMatch = matchCreateRequest.toEntity(homeTeam);
            Match savedMatch = matchRepository.save(createMatch);

            ApplicationTeamResponse response = modelMapper.map(savedMatch, ApplicationTeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else{
            return new ResponseEntity<>("해당 유저는 주장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //개설한 경기방 수정하기
    @PutMapping("/{matchId}")
    public ResponseEntity<?> modifyMatch(@PathVariable Long matchId,
                                         @CurrentAccount Account nowAccount,
                                         @RequestBody MatchModifyRequest matchModifyRequest){

        Match room = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 경기방입니다."));

        if(room.getHomeTeam().getOwner().getId().equals(nowAccount.getId())){
            room.modifyHomeInfo(matchModifyRequest);

            Match modifyMatch = matchRepository.save(room);

            ApplicationTeamResponse response = modelMapper.map(modifyMatch, ApplicationTeamResponse.class);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // HomeTeam에서의 수락 / 거절, 수락시 경기성사
    @PutMapping("/{matchId}/home/{applyTeamId}")
    public ResponseEntity<?> modifyHomeStatus(@PathVariable Long matchId, @PathVariable Long applyTeamId, @RequestBody MatchRequest matchRequest,
                                               @CurrentAccount Account nowAccount){
        Match room = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        ApplicationTeam applyTeam = applicationTeamRepository.findById(applyTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(room.getHomeTeam().getOwner().getId().equals(nowAccount.getId())){
            room.updateHomeStatus(HomeStatus.valueOf(matchRequest.getHomeStatus().name()));

            if(applyTeam.getAwayStatus().name().equals(AwayStatus.PENDING.name()) &&
                    room.getHomeStatus().name().equals(HomeStatus.ACCEPT.name())){

                Team awayTeam = applyTeam.getApplyTeams();

                // MatchRequest를 통하여 Match를 생성하고 repo에 저장한다.
                room.successMatch(awayTeam);

                Match saveMatch = matchRepository.save(room);
                MatchResponse response = modelMapper.map(saveMatch, MatchResponse.class);

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long matchId, @CurrentAccount Account nowAccount) {

        Match findMatch = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(findMatch.getHomeTeam().getOwner().getId().equals(nowAccount.getId())){
            matchRepository.delete(findMatch);

            MatchResponse response = modelMapper.map(findMatch, MatchResponse.class);

            return new ResponseEntity(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

    }


}
