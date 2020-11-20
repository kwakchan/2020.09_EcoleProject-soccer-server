package com.ksu.soccerserver.image;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.image.TeamImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/teams/images")
@RequiredArgsConstructor
public class TeamImageController {

    private final TeamImageService teamimageService;

    // 이미지업로드
    @PutMapping
    ResponseEntity<?> saveImage(@RequestParam("logo") MultipartFile image, HttpServletRequest request) {

        return teamimageService.saveImage(image, request);
    }
    // 이미지 불러오기
    @GetMapping(value = "/{imageName:.+}")
    ResponseEntity<?> getImage(@PathVariable String imageName, HttpServletRequest request) {
        return teamimageService.loadAsResource(imageName, request);
    }
    // 기본 이미지로 변경
    @PutMapping("/{teamId}")
    public ResponseEntity<?> removeImage(@PathVariable Long teamId, @CurrentAccount Account currentAccoun,
                                         HttpServletRequest request){
        return teamimageService.setTeamLogo(teamId, currentAccoun, request);
    }
}
