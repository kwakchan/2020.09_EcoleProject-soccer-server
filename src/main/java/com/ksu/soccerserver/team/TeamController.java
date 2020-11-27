package com.ksu.soccerserver.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.image.ImageService;
import com.ksu.soccerserver.team.dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final AccountRepository accountRepository;
    private final ImageService imageService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    // 팀 생성
    @PostMapping
    public ResponseEntity<?> createTeam(@CurrentAccount Account nowAccount, HttpServletRequest request,
                                        @RequestBody TeamRequest teamRequest){

        ServletUriComponentsBuilder defaultPath = ServletUriComponentsBuilder.fromCurrentContextPath();
        String requestUri = defaultPath.toUriString() + request.getRequestURI() + "/images/team_default.jpg";

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

                //TeamResponse response = modelMapper.map(madeTeam, TeamResponse.class);
                List<Account> accounts = accountRepository.findAllByTeam(madeTeam);
                TeamDTO response = new TeamDTO(madeTeam, accounts);
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

    //모든팀 Get
    @GetMapping
    public ResponseEntity<?> loadFilteredTeam(@RequestParam(required = false) String teamName,
                                              @RequestParam(required = false) String state,
                                              @RequestParam(required = false) String district){
        List<Team> teams;

        //(광역시, 시, 도)가 전체라면, 모든 팀을 검색
        if(state.equals("All")) {
            teams = teamRepository.findAllByNameContaining(teamName);
        }
        //(광역시, 시, 도)가 선택되었고, (구, 면, 읍)이 전체라면
        else if(district.equals("All")){
            teams = teamRepository.findAllByStateAndNameContaining(state, teamName);
        }
        //(광역시, 시, 도)가 선택되었고, (구, 면, 읍)또한 선택 시
        else {
            teams = teamRepository.findAllByStateAndDistrictAndNameContaining(state, district, teamName);
        }

        //TeamDTO List
        List<TeamDTO> tempDTOS = new ArrayList<>();
        FilteredTeamsDTO filteredTeamsDTO = new FilteredTeamsDTO();

        for(int i=0; i<teams.size(); i++) {
            TeamDTO teamDTO = new TeamDTO();
            teamDTO.setId(teams.get(i).getId());
            teamDTO.setName(teams.get(i).getName());
            teamDTO.setState(teams.get(i).getState());
            teamDTO.setDistrict(teams.get(i).getDistrict());
            teamDTO.setDescription(teams.get(i).getDescription());
            teamDTO.setLogopath(teams.get(i).getLogopath());
            teamDTO.setOwner(
                    new TeamsAccountDTO(teams.get(i).getOwner())
            );
            teamDTO.setAccounts(
                    new TeamsAccountsDTO(accountRepository.findAllByTeam(teams.get(i)))
            );

            tempDTOS.add(teamDTO);
        }
        filteredTeamsDTO.setFilteredTeamsDTO(tempDTOS);
        return new ResponseEntity<>(filteredTeamsDTO.getFilteredTeamsDTO(), HttpStatus.OK);

    }

    // 해당 teamId를 가진 팀 GET
    @GetMapping("/{teamId}")
    public ResponseEntity<?> loadTeam(@PathVariable Long teamId, @CurrentAccount Account nowAccount){
        Team findTeam = teamRepository.findById(teamId).orElseThrow
                (() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));

        if(nowAccount.getRoles().contains("ROLE_LEADER")) {
            if(!findTeam.getOwner().getId().equals(nowAccount.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<Account> accounts = accountRepository.findAllByTeam(findTeam);
            TeamDTO response = new TeamDTO(findTeam, accounts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        //TeamResponse response = modelMapper.map(findTeam, TeamResponse.class);
        List<Account> accounts = accountRepository.findAllByTeam(findTeam);
        TeamDTO response = new TeamDTO(findTeam, accounts);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 해당 팀의 정보 수정 => ROLE_LEADER
    @PutMapping("/{teamId}")
    public ResponseEntity<?> putTeam(@PathVariable Long teamId, @CurrentAccount Account nowAccount,
                                     @RequestPart(value = "logopath", required = false) MultipartFile image,
                                     @RequestPart(value = "data") String modifyTeam, HttpServletRequest request) throws JsonProcessingException {
        Team findTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다"));

        if(!findTeam.getOwner().getId().equals(nowAccount.getId())){
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        TeamModifyRequest teamModifyRequest = objectMapper.readValue(modifyTeam, TeamModifyRequest.class);
        if(image != null){
            String imagePath = imageService.saveImage(image, request);
            teamModifyRequest.setLogopath(imagePath);
        }

        findTeam.updateTeamInfo(teamModifyRequest);
        Team updatedTeam = teamRepository.save(findTeam);
        List<Account> accounts = accountRepository.findAllByTeam(updatedTeam);
        TeamDTO response = new TeamDTO(updatedTeam, accounts);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팀 삭제 => ROLE_LEADER
    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long teamId, @CurrentAccount Account nowAccount) {
        Team findTeam = teamRepository.findById(teamId).orElseThrow
                (() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."));
        if(!findTeam.getOwner().getId().equals(nowAccount.getId())){
            return new ResponseEntity<>("해당 유저는 팀장이 아닙니다.", HttpStatus.BAD_REQUEST);
        }else {
            teamRepository.delete(findTeam);

            //TeamResponse response = modelMapper.map(findTeam, TeamResponse.class);
            List<Account> accounts = accountRepository.findAllByTeam(findTeam);
            TeamDTO response = new TeamDTO(findTeam, accounts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
