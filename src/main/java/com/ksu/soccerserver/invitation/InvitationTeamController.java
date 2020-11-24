package com.ksu.soccerserver.invitation;


import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/invitations/teams")
@RequiredArgsConstructor
public class InvitationTeamController {
    private final InvitationTeamRepository invitationTeamRepository;
    private final TeamRepository teamRepository;

    @PostMapping
    public ResponseEntity<?> inviteTeam(@RequestBody InvitationTeamDto invitationTeamDto){
        Team homeTeam = teamRepository.findById(invitationTeamDto.getHomeTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        Team awayTeam = teamRepository.findById(invitationTeamDto.getAwayTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        InvitationTeam invite =
                InvitationTeam.builder().invitationHomeTeam(homeTeam).invitationAwayTeam(awayTeam)
                        .invitationStatus(InvitationStatus.INVITE_PENDING).build();

        invitationTeamRepository.save(invite);

        return new ResponseEntity<>(invite, HttpStatus.CREATED);
    }

    @GetMapping("/away/{teamId}")
    public ResponseEntity<?> loadInvitationAway(@PathVariable Long teamId){
        Team awayTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        List<InvitationTeam> invites = invitationTeamRepository.findByInvitationAwayTeam(awayTeam);

        return new ResponseEntity<>(invites, HttpStatus.OK);
    }

    @GetMapping("/home/{teamId}")
    public ResponseEntity<?> loadInvitationHome(@PathVariable Long teamId){
        Team homeTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        List<InvitationTeam> invites = invitationTeamRepository.findByInvitationHomeTeam(homeTeam);

        return new ResponseEntity<>(invites, HttpStatus.OK);
    }

    @PutMapping("/{invitationId}/{status}")
    public ResponseEntity<?> modifyInviteStatus(@PathVariable Long invitationId, @PathVariable String status){
        InvitationTeam invites = invitationTeamRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."));

        if(InvitationStatus.valueOf(status) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else { invites.updateStatus(InvitationStatus.valueOf(status)); }
        
        invitationTeamRepository.save(invites);

        return new ResponseEntity<>(invites, HttpStatus.OK);
    }

}
