package com.ksu.soccerserver.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "/api/accounts")
@RequiredArgsConstructor
public class AccountImageController {

    private final AccountImageService accountImageService;

    // Image Load
    @GetMapping({"/images/{imageName:.+}", "/{idx}/images/{imageName:.+}"})
    ResponseEntity<?> getImage(@PathVariable(name = "imageName") String imageName,
                               @PathVariable(name = "idx",required = false) Long idx,
                               HttpServletRequest request) {
        return accountImageService.loadAsResource(imageName, request);
    }
    // Image 초기화
//    @PutMapping("/{accountId}/images")
//    public ResponseEntity<?> removeImage(@PathVariable Long accountId, HttpServletRequest request){
//        return accountImageService.setuserImage(accountId, request);
//    }

}


