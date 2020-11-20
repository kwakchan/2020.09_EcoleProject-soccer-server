package com.ksu.soccerserver.image;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.config.JwtTokenProvider;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamImageService {

    private final Path rootLocation;

    private final JwtTokenProvider jwtProvider;

    private final TeamRepository teamRepository;

    private final AccountRepository accountRepository;

    public TeamImageService(ImageProperties imageProperties, JwtTokenProvider jwtProvider,
                            TeamRepository teamRepository, AccountRepository accountRepository) {
        this.rootLocation = Paths.get(imageProperties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        this.jwtProvider = jwtProvider;
        this.teamRepository = teamRepository;
        this.accountRepository = accountRepository;
    }


    public ResponseEntity<?> saveImage(MultipartFile image, HttpServletRequest request) {
        String imageName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(image.getOriginalFilename());
        String extension = FilenameUtils.getExtension(imageName);

        try {
            // image null 여부 확인
            if(image.isEmpty()){
                return new ResponseEntity<>("imageNullError", HttpStatus.BAD_REQUEST);
            }

            if (!"jpg".equals(extension) && !"jpeg".equals(extension) && !"png".equals(extension)) {
                return new ResponseEntity<>("TypeError", HttpStatus.BAD_REQUEST);
            }
            // message-body로 넘어온 parmeter 확인
            try (InputStream inputStream = image.getInputStream()) {
                Path targetLocation = this.rootLocation.resolve(imageName);
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

        }catch (IOException e){
            return new ResponseEntity<>("Failed to Store file" + imageName,HttpStatus.BAD_REQUEST);
        }

        String email = jwtProvider.getUserPk(request.getHeader(HttpHeaders.AUTHORIZATION));
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("userError", HttpStatus.BAD_REQUEST);
        }

        String requestUri = request.getRequestURI() + "/";
        Team team = accountOptional.get().getTeam();
        String newLogoPath = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(requestUri)
                .path(imageName)
                .toUriString();

        team.setLogo(newLogoPath);
        return new ResponseEntity<>(teamRepository.save(team), HttpStatus.CREATED);
    }

    public ResponseEntity<?> loadAsResource(String imageName, HttpServletRequest request) {
        try {
            Path imagePath = load(imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            // exists : 데이터 존재 확인, isReadable : Resource를 읽을 수 있는지 확인
            if (resource.exists() || resource.isReadable()) {
                String contentType = null;
                try {
                    contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                } catch (IOException e) {
                    return new ResponseEntity<>("TypeError", HttpStatus.BAD_REQUEST);
                }
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                // 이미지 출력
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                // 데이터가 존재 하지 않을 경우 오류 출력
                return new ResponseEntity<>("Nullerror", HttpStatus.BAD_REQUEST);
            }
        } catch (MalformedURLException e) {
            return new ResponseEntity<>("findError", HttpStatus.BAD_REQUEST);
        }
    }

    private Path load(String imageName) {
        return this.rootLocation.resolve(imageName).normalize();
    }
    
}
