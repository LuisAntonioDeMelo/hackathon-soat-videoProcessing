package com.hackton.soat.videoprocessor.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoEntityTest {

    @Test
    void constructor_NoArgs_ShouldCreateEmptyEntity() {
        // Act
        VideoEntity entity = new VideoEntity();

        // Assert
        assertNotNull(entity);
        assertNull(entity.getUuid());
        assertNull(entity.getName());
        assertNull(entity.getPath());
        assertNull(entity.getTimestamp());
        assertNull(entity.getStatus());
        assertNull(entity.getZipPath());
        assertNull(entity.getFrameCount());
        assertNull(entity.getImagesJson());
    }

    @Test
    void constructor_AllArgs_ShouldCreateEntityWithAllFields() {
        // Arrange
        UUID testUuid = UUID.randomUUID();
        String testName = "test-video.mp4";
        String testPath = "/path/to/video.mp4";
        LocalDateTime testTime = LocalDateTime.now();
        String testStatus = "CONCLUIDO";
        String testZipPath = "/path/to/frames.zip";
        Integer testFrameCount = 100;
        String testImagesJson = "[\"frame1.png\", \"frame2.png\"]";

        // Act
        VideoEntity entity = new VideoEntity(
                testUuid, testName, testPath, testTime, testStatus, 
                testZipPath, testFrameCount, testImagesJson
        );

        // Assert
        assertNotNull(entity);
        assertEquals(testUuid, entity.getUuid());
        assertEquals(testName, entity.getName());
        assertEquals(testPath, entity.getPath());
        assertEquals(testTime, entity.getTimestamp());
        assertEquals(testStatus, entity.getStatus());
        assertEquals(testZipPath, entity.getZipPath());
        assertEquals(testFrameCount, entity.getFrameCount());
        assertEquals(testImagesJson, entity.getImagesJson());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        VideoEntity entity = new VideoEntity();
        UUID testUuid = UUID.randomUUID();
        String testName = "test-video.mp4";
        String testPath = "/path/to/video.mp4";
        LocalDateTime testTime = LocalDateTime.now();
        String testStatus = "PROCESSANDO";
        String testZipPath = "/path/to/frames.zip";
        Integer testFrameCount = 50;
        String testImagesJson = "[\"frame1.png\"]";

        // Act
        entity.setUuid(testUuid);
        entity.setName(testName);
        entity.setPath(testPath);
        entity.setTimestamp(testTime);
        entity.setStatus(testStatus);
        entity.setZipPath(testZipPath);
        entity.setFrameCount(testFrameCount);
        entity.setImagesJson(testImagesJson);

        // Assert
        assertEquals(testUuid, entity.getUuid());
        assertEquals(testName, entity.getName());
        assertEquals(testPath, entity.getPath());
        assertEquals(testTime, entity.getTimestamp());
        assertEquals(testStatus, entity.getStatus());
        assertEquals(testZipPath, entity.getZipPath());
        assertEquals(testFrameCount, entity.getFrameCount());
        assertEquals(testImagesJson, entity.getImagesJson());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        
        VideoEntity entity1 = new VideoEntity();
        entity1.setUuid(uuid1);
        entity1.setName("video1.mp4");
        
        VideoEntity entity2 = new VideoEntity();
        entity2.setUuid(uuid1);
        entity2.setName("video1.mp4");
        
        VideoEntity entity3 = new VideoEntity();
        entity3.setUuid(uuid2);
        entity3.setName("video2.mp4");

        // Assert
        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1, null);
        assertNotEquals(entity1, "not a VideoEntity");
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        VideoEntity entity1 = new VideoEntity();
        entity1.setUuid(uuid);
        entity1.setName("video.mp4");
        
        VideoEntity entity2 = new VideoEntity();
        entity2.setUuid(uuid);
        entity2.setName("video.mp4");

        // Assert
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void statusTransitions_ShouldWorkCorrectly() {
        // Arrange
        VideoEntity entity = new VideoEntity();
        
        // Act & Assert - Test status transitions
        entity.setStatus("Uploaded");
        assertEquals("Uploaded", entity.getStatus());
        
        entity.setStatus("processando");
        assertEquals("processando", entity.getStatus());
        
        entity.setStatus("CONCLUIDO");
        assertEquals("CONCLUIDO", entity.getStatus());
        
        entity.setStatus("erro");
        assertEquals("erro", entity.getStatus());
    }
}
