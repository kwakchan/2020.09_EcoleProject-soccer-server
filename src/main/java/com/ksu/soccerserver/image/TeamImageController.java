package com.ksu.soccerserver.image;

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

    @PostMapping
    ResponseEntity<?> saveImage(@RequestParam("logo") MultipartFile image, HttpServletRequest request) {

        return teamimageService.saveImage(image, request);
    }
    @GetMapping(value = "/{imageName:.+}")
    ResponseEntity<?> getImage(@PathVariable String imageName, HttpServletRequest request) {
        return teamimageService.loadAsResource(imageName, request);
    }
}
