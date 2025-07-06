package com.hackton.soat.videoprocessor.controller;

import com.hackton.soat.videoprocessor.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

}
