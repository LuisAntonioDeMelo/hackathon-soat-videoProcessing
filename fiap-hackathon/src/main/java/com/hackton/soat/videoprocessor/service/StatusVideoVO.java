package com.hackton.soat.videoprocessor.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusVideoVO {
    private String id;
    private String status;

    public static StatusVideoVO of(String id, String status) {
        return StatusVideoVO.builder()
                .id(id)
                .status(status)
                .build();
    }
}
