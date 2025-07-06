package com.hackton.soat.videoprocessor.message.pojo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoProcessRequest {
    private String videoPath;
    private String timestamp;
    private MultipartFile videoFile;
}
