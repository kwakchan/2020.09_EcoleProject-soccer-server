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
import com.ksu.soccerserver.match.enums.MatchStatus;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
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

    // 개설된 모든 경기방 및 필터링된 경기방 GET
    @GetMapping
    public ResponseEntity<?> loadApplications(@RequestParam(required = false) String teamName,
                                              @RequestParam(required = false) String state,
                                              @RequestParam(required = false) String district){

        List<MatchResponse> matchResponses;

        if("All".equals(state)) {
            matchResponses = matchRepository.findAllByMatchStatus(MatchStatus.PENDING)
                    .stream()
                    .filter(match -> match.getHomeTeam().getName().contains(teamName))
                    .map(match ->
                            new MatchResponse(match))
                    .collect(Collectors.toList());
        } else if ("All".equals(district)){
            matchResponses = matchRepository.findAllByMatchStatus(MatchStatus.PENDING)
                    .stream()
                    .filter(match -> match.getHomeTeam().getName().contains(teamName)
                        && match.getState().equals(state))
                    .map(match ->
                            new MatchResponse(match))
                    .collect(Collectors.toList());
        } else {
            matchResponses = matchRepository.findAllByMatchStatus(MatchStatus.PENDING)
                    .stream()
                    .filter(match -> match.getHomeTeam().getName().contains(teamName)
                        && match.getState().equals(state)
                        && match.getDistrict().equals(district))
                    .map(match ->
                            new MatchResponse(match))
                    .collect(Collectors.toList());
        }
        return new ResponseEntity<>(matchResponses, HttpStatus.OK);
    }

    // 내가 만든 (우리팀의) 경기방 목록
    @GetMapping("/homeTeam/matchList")
    public ResponseEntity<?> loadMyMatching( @CurrentAccount Account nowAccount) {
        Team homeTeam = teamRepository.findByAccounts(nowAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않거나 팀에 소속되어 있지 않습니다."));

        List<MatchResponse> myMatching = matchRepository.findAllByHomeTeam(homeTeam)
                    .stream().map(match -> new MatchResponse(match)).collect(Collectors.toList());

        return new ResponseEntity<>(myMatching, HttpStatus.OK);
    }

    //특정 경기방 상세 정보 보기
    @GetMapping("/{matchId}")
    public ResponseEntity<?> loadApplication(@PathVariable Long matchId) {
        Match application = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 방입니다."));

        MatchResponse response = new MatchResponse(application);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 자신의 팀에서 요청받은 경기에 대해 리스트를 보는 api => ROLE_LEADER
    @GetMapping("home/{matchId}")
    public ResponseEntity<?> loadApplicationHomes(@PathVariable Long matchId,
                                                  @CurrentAccount Account nowAccount){
        Match findMatch = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 경기방 입니다."));

        if(findMatch.getHomeTeam().getId().equals(nowAccount.getTeam().getId())) {
            List<ApplicationTeamResponse> applies = applicationTeamRepository.findAllByMatchId(matchId)
                    .stream().map(apply -> new ApplicationTeamResponse(apply)).collect(Collectors.toList());

            return new ResponseEntity<>(applies, HttpStatus.OK);
        } else{
            return new ResponseEntity<>("해당 경기 개설 팀의 소속인원이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

    }

    //경기방 개설 => ROLE_LEADER
    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody MatchCreateRequest matchCreateRequest,
                                         @CurrentAccount Account nowAccount){

        Team homeTeam = teamRepository.findById(nowAccount.getLeadingTeam().getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(homeTeam.getOwner().getId().equals(nowAccount.getId())) {

            Match createMatch = matchCreateRequest.toEntity(homeTeam);
            Match savedMatch = matchRepository.save(createMatch);

            MatchResponse response = new MatchResponse(savedMatch);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else{
            return new ResponseEntity<>("해당 유저는 팀팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //개설한 경기방 수정하기 => ROLE_LEADER
    @PutMapping("/{matchId}")
    public ResponseEntity<?> modifyMatch(@PathVariable Long matchId,
                                         @CurrentAccount Account nowAccount,
                                         @RequestBody MatchModifyRequest matchModifyRequest){

        Match room = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 경기방입니다."));

        if(room.getHomeTeam().getOwner().getId().equals(nowAccount.getId())){
            room.modifyHomeInfo(matchModifyRequest);

            Match modifyMatch = matchRepository.save(room);

            MatchResponse response =
                    new MatchResponse(modifyMatch);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // HomeTeam에서의 수락 / 거절, 수락시 경기성사 => ROLE_LEADER
    @PutMapping("/{matchId}/home/{applyTeamId}")
    public ResponseEntity<?> modifyHomeStatus(@PathVariable Long matchId, @PathVariable Long applyTeamId,
                                              @RequestBody MatchRequest matchRequest,
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
                MatchResponse response = new MatchResponse(saveMatch);

                applicationTeamRepository.findAllByMatchId(matchId)
                        .stream()
                        .filter(applicationTeam -> !applicationTeam.getApplyTeams().getId().equals(awayTeam.getId()))
                        .map(ApplicationTeam::cancelApplication);

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{matchId}") // => ROLE_LEADER
    public ResponseEntity<?> deleteMatch(@PathVariable Long matchId, @CurrentAccount Account nowAccount) {

        Match findMatch = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(findMatch.getHomeTeam().getOwner().getId().equals(nowAccount.getId())){
            matchRepository.delete(findMatch);

            MatchResponse response = new MatchResponse(findMatch);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }


}
