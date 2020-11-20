package com.ksu.soccerserver.application;


import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
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

    // 자신의 팀에서 신청한 경기에 대해 리스트를 보는 api
    @GetMapping("/away/{teamId}")
    public ResponseEntity<?> loadApplicationAway(@PathVariable Long teamId){
        Team awayTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        List<ApplicationTeam> applies = applicationTeamRepository.findByApplicationAwayTeam(awayTeam);

        return new ResponseEntity<>(applies, HttpStatus.OK);
    }

    // 자신의 팀에서 요청한 경기에 대해 리스트를 보는 api
    @GetMapping("/home/{teamId}")
    public ResponseEntity<?> loadApplicationHome(@PathVariable Long teamId){
        Team homeTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        List<ApplicationTeam> applies = applicationTeamRepository.findByApplicationHomeTeam(homeTeam);

        return new ResponseEntity<>(applies, HttpStatus.OK);
    }


    // awayTeam -> homeTeam 경기신청
    @PostMapping
    public ResponseEntity<?> applyTeam(@RequestBody ApplicationTeamDto applicationTeamDto){

        Team homeTeam = teamRepository.findById(applicationTeamDto.getHomeTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        Team awayTeam = teamRepository.findById(applicationTeamDto.getAwayTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        ApplicationTeam apply =
                ApplicationTeam.builder().applicationHomeTeam(homeTeam).applicationAwayTeam(awayTeam)
                .applicationStatus(ApplicationStatus.APPLY_PENDING).build();

        applicationTeamRepository.save(apply);

        return new ResponseEntity<>(apply, HttpStatus.CREATED);
    }

    @PutMapping("/{applicationId}/{status}")
    public ResponseEntity<?> modifyApplyStatus(@PathVariable Long applicationId, @PathVariable String status){
        ApplicationTeam apply = applicationTeamRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(ApplicationStatus.valueOf(status) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else { apply.updateStatus(ApplicationStatus.valueOf(status)); }

        applicationTeamRepository.save(apply);

        return new ResponseEntity<>(apply, HttpStatus.OK);
    }

}
