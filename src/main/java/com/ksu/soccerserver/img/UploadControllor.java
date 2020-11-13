package com.ksu.soccerserver.img;

import com.ksu.soccerserver.img.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadControllor {
    @Autowired
    ImageService imageService;

    @Autowired
    ResourceLoader resourceLoader;

    @PostMapping("/update")
    public ResponseEntity<?> imageUpload(MultipartFile file) {
        try {
            Upload upload = imageService.store(file);
            return ResponseEntity.ok().body("/image/" + upload.getId());
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/update/{fileId}")
    public ResponseEntity<?> serveFile(@PathVariable Long fileId){
        try {
            Upload upload = imageService.load(fileId);
            Path path = Paths.get(upload.getFilePath());
            Resource resource = new InputStreamResource(Files.newInputStream(path));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + upload.getSaveFileName() + "\"")
                    .body(resource);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }


}
