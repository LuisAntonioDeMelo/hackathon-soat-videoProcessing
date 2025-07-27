package com.hackton.soat.videoprocessor.message.pojo;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class VideoProcessRequestTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        VideoProcessRequest request = new VideoProcessRequest();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", "content".getBytes()
        );

        // Act
        request.setId("123e4567-e89b-12d3-a456-426614174000");
        request.setVideoPath("/path/to/video.mp4");
        request.setTimestamp("2024-01-01 10:00:00");
        request.setVideoFile(mockFile);

        // Assert
        assertEquals("123e4567-e89b-12d3-a456-426614174000", request.getId());
        assertEquals("/path/to/video.mp4", request.getVideoPath());
        assertEquals("2024-01-01 10:00:00", request.getTimestamp());
        assertEquals(mockFile, request.getVideoFile());
    }

    @Test
    void constructor_ShouldCreateEmptyRequest() {
        // Act
        VideoProcessRequest request = new VideoProcessRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getId());
        assertNull(request.getVideoPath());
        assertNull(request.getTimestamp());
        assertNull(request.getVideoFile());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Arrange
        VideoProcessRequest request1 = new VideoProcessRequest();
        request1.setId("id1");
        request1.setVideoPath("/path1");
        request1.setTimestamp("timestamp1");

        VideoProcessRequest request2 = new VideoProcessRequest();
        request2.setId("id1");
        request2.setVideoPath("/path1");
        request2.setTimestamp("timestamp1");

        VideoProcessRequest request3 = new VideoProcessRequest();
        request3.setId("id2");
        request3.setVideoPath("/path1");
        request3.setTimestamp("timestamp1");

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // Arrange
        VideoProcessRequest request1 = new VideoProcessRequest();
        request1.setId("id1");
        request1.setVideoPath("/path1");

        VideoProcessRequest request2 = new VideoProcessRequest();
        request2.setId("id1");
        request2.setVideoPath("/path1");

        // Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Arrange
        VideoProcessRequest request = new VideoProcessRequest();
        request.setId("test-id");
        request.setVideoPath("/test/path");
        request.setTimestamp("test-timestamp");

        // Act
        String result = request.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("id"));
        assertTrue(result.contains("videoPath"));
        assertTrue(result.contains("timestamp"));
    }

    @Test
    void withNullValues_ShouldHandleGracefully() {
        // Arrange
        VideoProcessRequest request = new VideoProcessRequest();

        // Act - setting null values
        request.setId(null);
        request.setVideoPath(null);
        request.setTimestamp(null);
        request.setVideoFile(null);

        // Assert
        assertNull(request.getId());
        assertNull(request.getVideoPath());
        assertNull(request.getTimestamp());
        assertNull(request.getVideoFile());
    }
}
