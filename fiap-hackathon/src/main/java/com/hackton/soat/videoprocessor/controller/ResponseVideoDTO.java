package com.hackton.soat.videoprocessor.controller;

import com.hackton.soat.videoprocessor.model.VideoEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseVideoDTO {
    private String message;
    private String id;
    private String tempoDeProcessamento;
    private String videoPath;
    private String videoName;
    private String videoSize;
    private String tumbnail;

    public static ResponseVideoDTO of(VideoEntity videoEntity) {
        return ResponseVideoDTO.builder()
                .message("Video processed successfully")
                .id(videoEntity.getUuid().toString())
                .videoPath(videoEntity.getPath())
                .videoName(videoEntity.getName())
                .videoSize(String.valueOf(videoEntity.getFrameCount()))
                .tumbnail(videoEntity.getZipPath())
                .tempoDeProcessamento(String.valueOf(videoEntity.getTimestamp()))
                .build();
    }
}
