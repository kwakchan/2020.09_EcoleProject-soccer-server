package com.ksu.soccerserver.account;

import com.ksu.soccerserver.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "/api/images")
@RequiredArgsConstructor
public class AccountImageController {

    private final ImageService imageService;

    @PostMapping
    ResponseEntity<?> saveImage(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        return imageService.saveImage(image, request);
    }


    @GetMapping(value = "/{imageName:.+}")
    ResponseEntity<?> getImage(@PathVariable String imageName, HttpServletRequest request) {
        return imageService.loadAsResource(imageName, request);
    }

}

