package com.hackton.soat.videoprocessor.message.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoVO {
    private String videoPath;
    private String timestamp;
    private String videoName;
    private String videoSize;
    private String status;
}
