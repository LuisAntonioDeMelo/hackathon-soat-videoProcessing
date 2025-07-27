package com.hackton.soat.videoprocessor.controller;

import com.hackton.soat.videoprocessor.model.VideoEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResponseVideoDTOTest {

    @Test
    void of_WithVideoEntity_ShouldCreateCorrectDTO() {
        // Arrange
        UUID testUuid = UUID.randomUUID();
        LocalDateTime testTime = LocalDateTime.now();
        
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setUuid(testUuid);
        videoEntity.setName("test-video.mp4");
        videoEntity.setPath("/path/to/video.mp4");
        videoEntity.setTimestamp(testTime);
        videoEntity.setFrameCount(100);
        videoEntity.setZipPath("/path/to/frames.zip");

        // Act
        ResponseVideoDTO result = ResponseVideoDTO.of(videoEntity);

        // Assert
        assertNotNull(result);
        assertEquals("Video processed successfully", result.getMessage());
        assertEquals(testUuid.toString(), result.getId());
        assertEquals("/path/to/video.mp4", result.getVideoPath());
        assertEquals("test-video.mp4", result.getVideoName());
        assertEquals("100", result.getVideoSize());
        assertEquals("/path/to/frames.zip", result.getTumbnail());
        assertEquals(testTime.toString(), result.getTempoDeProcessamento());
    }

    @Test
    void builder_ShouldCreateCorrectDTO() {
        // Act
        ResponseVideoDTO result = ResponseVideoDTO.builder()
                .message("Test message")
                .id("test-id")
                .videoName("test.mp4")
                .videoPath("/test/path")
                .videoSize("1024 bytes")
                .tumbnail("/test/thumbnail")
                .tempoDeProcessamento("2024-01-01T10:00:00")
                .build();

        // Assert
        assertNotNull(result);
        assertEquals("Test message", result.getMessage());
        assertEquals("test-id", result.getId());
        assertEquals("test.mp4", result.getVideoName());
        assertEquals("/test/path", result.getVideoPath());
        assertEquals("1024 bytes", result.getVideoSize());
        assertEquals("/test/thumbnail", result.getTumbnail());
        assertEquals("2024-01-01T10:00:00", result.getTempoDeProcessamento());
    }

    @Test
    void of_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setUuid(UUID.randomUUID());
        // Leave other fields null

        // Act
        ResponseVideoDTO result = ResponseVideoDTO.of(videoEntity);

        // Assert
        assertNotNull(result);
        assertEquals("Video processed successfully", result.getMessage());
        assertNotNull(result.getId());
        assertNull(result.getVideoPath());
        assertNull(result.getVideoName());
        assertEquals("null", result.getVideoSize()); // String.valueOf(null) returns "null"
        assertNull(result.getTumbnail());
        assertEquals("null", result.getTempoDeProcessamento());
    }
}
