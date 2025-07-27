package com.hackton.soat.videoprocessor.message.pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoVOTest {

    @Test
    void builder_ShouldCreateCorrectVO() {
        // Act
        VideoVO videoVO = VideoVO.builder()
                .videoPath("/path/to/video.mp4")
                .timestamp("2024-01-01 10:00:00")
                .videoName("test-video.mp4")
                .videoSize("1024 bytes")
                .status("Uploaded")
                .build();

        // Assert
        assertNotNull(videoVO);
        assertEquals("/path/to/video.mp4", videoVO.getVideoPath());
        assertEquals("2024-01-01 10:00:00", videoVO.getTimestamp());
        assertEquals("test-video.mp4", videoVO.getVideoName());
        assertEquals("1024 bytes", videoVO.getVideoSize());
        assertEquals("Uploaded", videoVO.getStatus());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        VideoVO videoVO = VideoVO.builder().build();

        // Act
        videoVO.setVideoPath("/new/path/video.mp4");
        videoVO.setTimestamp("2024-01-02 11:00:00");
        videoVO.setVideoName("new-video.mp4");
        videoVO.setVideoSize("2048 bytes");
        videoVO.setStatus("Processing");

        // Assert
        assertEquals("/new/path/video.mp4", videoVO.getVideoPath());
        assertEquals("2024-01-02 11:00:00", videoVO.getTimestamp());
        assertEquals("new-video.mp4", videoVO.getVideoName());
        assertEquals("2048 bytes", videoVO.getVideoSize());
        assertEquals("Processing", videoVO.getStatus());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Arrange
        VideoVO vo1 = VideoVO.builder()
                .videoPath("/path/video.mp4")
                .videoName("video.mp4")
                .status("Uploaded")
                .build();

        VideoVO vo2 = VideoVO.builder()
                .videoPath("/path/video.mp4")
                .videoName("video.mp4")
                .status("Uploaded")
                .build();

        VideoVO vo3 = VideoVO.builder()
                .videoPath("/different/path/video.mp4")
                .videoName("video.mp4")
                .status("Uploaded")
                .build();

        // Assert
        assertEquals(vo1, vo2);
        assertNotEquals(vo1, vo3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // Arrange
        VideoVO vo1 = VideoVO.builder()
                .videoPath("/path/video.mp4")
                .videoName("video.mp4")
                .build();

        VideoVO vo2 = VideoVO.builder()
                .videoPath("/path/video.mp4")
                .videoName("video.mp4")
                .build();

        // Assert
        assertEquals(vo1.hashCode(), vo2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Arrange
        VideoVO videoVO = VideoVO.builder()
                .videoPath("/path/to/video.mp4")
                .timestamp("2024-01-01 10:00:00")
                .videoName("test-video.mp4")
                .videoSize("1024 bytes")
                .status("Uploaded")
                .build();

        // Act
        String result = videoVO.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("videoPath"));
        assertTrue(result.contains("timestamp"));
        assertTrue(result.contains("videoName"));
        assertTrue(result.contains("videoSize"));
        assertTrue(result.contains("status"));
    }
}
