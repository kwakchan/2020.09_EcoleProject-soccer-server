package com.ksu.soccerserver.team;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.team.dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/getAccount")
    public ResponseEntity<?> getAccounts() {
        List<Account> accounts =accountRepository.findAllByTeam(teamRepository.findById((long)1).get());

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    /* 프론트 앤드 팀 리스트 담당자의 테스트를 위한 목적으로 임시 팀원 삽입 메소드 구현 */
    @PostMapping("/{teamId}")
    public ResponseEntity<?> practiceTeam(@CurrentAccount Account joiningAccount, @PathVariable Long teamId){
        Team team = teamRepository.findById(teamId).get();
        team.joinMember(joiningAccount);

        joiningAccount.setTeam(team);
        accountRepository.save(joiningAccount);

        return new ResponseEntity<>(teamRepository.save(team), HttpStatus.OK);
    }


    //모든팀 Get
    @GetMapping
    public ResponseEntity<?> loadFilteredTeam(@RequestParam(required = false) String teamName,
                                              @RequestParam String state,
                                              @RequestParam String district){
        List<Team> teams;
        //(광역시, 시, 도)가 전체라면, 모든 팀을 검색
        if(state.equals("All")) {
            teams = teamRepository.findAll();
        }
        //(광역시, 시, 도)가 선택되었고, (구, 면, 읍)이 전체라면
        else if(district.equals("All")){
            teams = teamRepository.findAllByState(state);
        }
        //(광역시, 시, 도)가 선택되었고, (구, 면, 읍)또한 선택 시
        else {
            teams = teamRepository.findAllByStateAndDistrict(state, district);
        }


        //List<Team> teams = teamRepository.findAll();
        TeamDTO teamDTO = new TeamDTO();
        //TeamDTO List
        List<TeamDTO> tempDTOS = new ArrayList<>();
        FilteredTeamsDTO filteredTeamsDTO = new FilteredTeamsDTO();

        for(int i=0; i<teams.size(); i++) {
            teamDTO.setId(teams.get(i).getId());
            teamDTO.setName(teams.get(i).getName());
            teamDTO.setState(teams.get(i).getState());
            teamDTO.setDistrict(teams.get(i).getDistrict());
            teamDTO.setDescription(teams.get(i).getDescription());
            teamDTO.setLogopath(teams.get(i).getLogopath());
            teamDTO.setOwner(
                    //teams.get(i).getOwner()
                    new TeamsAccountDTO(teams.get(i).getOwner())
            );
            teamDTO.setAccounts(
                    //accountRepository.findAllByTeam(teams.get(i))
                    new TeamsAccountsDTO(accountRepository.findAllByTeam(teams.get(i)))
            );

            tempDTOS.add(teamDTO);
        }
        filteredTeamsDTO.setFilteredTeamsDTO(tempDTOS);
        return new ResponseEntity<>(filteredTeamsDTO.getFilteredTeamsDTO(), HttpStatus.OK);

    }

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

        if(!findTeam.getOwner().getId().equals(nowAccount.getId())){
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
