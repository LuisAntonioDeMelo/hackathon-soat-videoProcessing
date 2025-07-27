package com.hackton.soat.videoprocessor.controller;

import com.hackton.soat.videoprocessor.service.StatusVideoVO;
import com.hackton.soat.videoprocessor.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

     @PostMapping("/upload")
     public ResponseEntity<?> uploadVideo(@RequestParam(value = "file", required = false) MultipartFile file) {
         if(file == null || file.isEmpty()) {
             return ResponseEntity.badRequest().body(ResponseVideoDTO.builder().message("File is empty").build());
         }
         ResponseVideoDTO responseVideoDTO = videoService.uploadVideo(file);
         return ResponseEntity.ok(responseVideoDTO);
     }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable String  id) {
        try {
            StatusVideoVO status = videoService.obterStatus(id);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadZip(@PathVariable String id) {
        try {
            Resource resource = videoService.downloadZip(id);

            String filename = "frames_" + id + ".zip";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
