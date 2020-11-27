package com.ksu.soccerserver.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/accounts")
@RequiredArgsConstructor
public class AccountImageController {

    private final ImageService imageService;

    // Image Load
    @GetMapping({"/images/{imageName:.+}", "/{idx}/images/{imageName:.+}"})
    ResponseEntity<?> getImage(@PathVariable(name = "imageName") String imageName,
                               @PathVariable(name = "idx",required = false) Long idx,
                               HttpServletRequest request) {
        return imageService.loadAsResource(imageName, request);
    }

}


