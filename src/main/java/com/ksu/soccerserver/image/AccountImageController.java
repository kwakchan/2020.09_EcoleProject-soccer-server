package com.ksu.soccerserver.image;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.image.AccountImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/accounts/images")
@RequiredArgsConstructor
public class AccountImageController {

    private final AccountImageService accountImageService;

    @PostMapping
    ResponseEntity<?> saveImage(@RequestParam(name = "image", required = false) MultipartFile image, HttpServletRequest request) throws MalformedURLException {
        return accountImageService.saveImage(image, request);
    }

    @GetMapping(value = "/{imageName:.+}")
    ResponseEntity<?> getImage(@PathVariable String imageName, HttpServletRequest request) {
        return accountImageService.loadAsResource(imageName, request);
    }



}


