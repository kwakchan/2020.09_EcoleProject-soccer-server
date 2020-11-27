package com.ksu.soccerserver.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/teams")
@RequiredArgsConstructor
public class TeamImageController {

    private final ImageService imageService;

    @GetMapping({"/images/{imageName:.+}", "/{idx}/images/{imageName:.+}"})
    ResponseEntity<?> getImage(@PathVariable(name = "imageName") String imageName,
                               @PathVariable(name = "idx",required = false) Long idx,
                               HttpServletRequest request) {
        return imageService.loadAsResource(imageName, request);
    }
}
