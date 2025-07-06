package com.hackton.soat.videoprocessor.controller;

import com.hackton.soat.videoprocessor.service.VideoService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

     @PostMapping("/upload")
     public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
         if(file.isEmpty()) {
             return ResponseEntity.badRequest().body("File is empty");
         }
         ResponseVideoDTO responseVideoDTO = videoService.uploadVideo(file);
         return ResponseEntity.ok(responseVideoDTO);
     }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable String  id) {
        return videoService.status(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


//    @GetMapping("/download/{id}")
//    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
//        var job = repo.findById(id).orElseThrow();
//        Resource file = new FileSystemResource(job.getZipPath());
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename())
//                .body(file);
//    }

}
