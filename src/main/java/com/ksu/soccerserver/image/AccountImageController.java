package com.ksu.soccerserver.image;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.account.dto.AccountModifyRequest;
import com.ksu.soccerserver.account.dto.AccountResponse;
import com.ksu.soccerserver.image.AccountImageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
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
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    // Image Upload
    @PutMapping
    ResponseEntity<?> saveImage(@RequestParam(name = "image", required = false) MultipartFile image, HttpServletRequest request) throws MalformedURLException {
        return accountImageService.saveImage(image, request);
    }
    // Image Load
    @GetMapping(value = "/{imageName:.+}")
    ResponseEntity<?> getImage(@PathVariable String imageName, HttpServletRequest request) {
        return accountImageService.loadAsResource(imageName, request);
    }
    // Image 초기화
    @PutMapping("/{accountId}")
    public ResponseEntity<?> removeImage(@PathVariable Long accountId, HttpServletRequest request){
        return accountImageService.setuserImage(accountId, request);
    }

}


