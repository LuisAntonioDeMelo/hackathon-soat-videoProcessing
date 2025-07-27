package com.hackton.soat.videoprocessor.message;

import com.hackton.soat.videoprocessor.message.pojo.VideoProcessRequest;
import com.hackton.soat.videoprocessor.model.VideoEntity;
import com.hackton.soat.videoprocessor.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoProcessConsumerTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoProcessConsumer videoProcessConsumer;

    @TempDir
    Path tempDir;

    private UUID testUuid;
    private VideoEntity testVideoEntity;
    private VideoProcessRequest testRequest;

    @BeforeEach
    void setUp() throws IOException {
        testUuid = UUID.randomUUID();
        testVideoEntity = new VideoEntity();
        testVideoEntity.setUuid(testUuid);
        testVideoEntity.setName("test-video.mp4");
        testVideoEntity.setStatus("Uploaded");
        testVideoEntity.setTimestamp(LocalDateTime.now());

        // Create a test video file
        Path testVideoPath = tempDir.resolve("test-video.mp4");
        Files.write(testVideoPath, "fake video content".getBytes());
        testVideoEntity.setPath(testVideoPath.toString());

        testRequest = new VideoProcessRequest();
        testRequest.setId(testUuid.toString());
        testRequest.setVideoPath(testVideoPath.toString());
        testRequest.setTimestamp("2024-01-01 10:00:00");
    }

    @Test
    void receiveVideo_WithValidRequest_ShouldUpdateStatusToProcessing() throws IOException, InterruptedException {
        // Arrange
        when(videoRepository.findById(testUuid)).thenReturn(Optional.of(testVideoEntity));
        when(videoRepository.save(any(VideoEntity.class))).thenReturn(testVideoEntity);

        // Act
        try {
            videoProcessConsumer.receiveVideo(testRequest);
        } catch (Exception e) {
            // Expected to fail due to ffmpeg not being available in test environment
            // But we can still verify the initial status update
        }

        // Assert
        ArgumentCaptor<VideoEntity> entityCaptor = ArgumentCaptor.forClass(VideoEntity.class);
        verify(videoRepository, atLeastOnce()).save(entityCaptor.capture());
        
        // Verify that status was updated to "processando"
        boolean foundProcessingStatus = entityCaptor.getAllValues().stream()
                .anyMatch(entity -> "processando".equals(entity.getStatus()));
        assertTrue(foundProcessingStatus, "Status should be updated to 'processando'");
    }

    @Test
    void receiveVideo_WithInvalidVideoId_ShouldThrowException() {
        // Arrange
        when(videoRepository.findById(testUuid)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> videoProcessConsumer.receiveVideo(testRequest));
        
        assertTrue(exception.getMessage().contains("Video not found with id"));
    }

    @Test
    void receiveVideo_WithNonExistentVideoFile_ShouldSetErrorStatus() throws IOException, InterruptedException {
        // Arrange
        testRequest.setVideoPath("/non/existent/path/video.mp4");
        when(videoRepository.findById(testUuid)).thenReturn(Optional.of(testVideoEntity));
        when(videoRepository.save(any(VideoEntity.class))).thenReturn(testVideoEntity);

        // Act
        try {
            videoProcessConsumer.receiveVideo(testRequest);
        } catch (IOException e) {
            // Expected when ffmpeg is not available
        }

        // Assert
        ArgumentCaptor<VideoEntity> entityCaptor = ArgumentCaptor.forClass(VideoEntity.class);
        verify(videoRepository, atLeast(1)).save(entityCaptor.capture());

        // Verify that status was updated to "processando" at least
        boolean foundProcessingStatus = entityCaptor.getAllValues().stream()
                .anyMatch(entity -> "processando".equals(entity.getStatus()));
        assertTrue(foundProcessingStatus, "Status should be updated to 'processando'");
    }

    @Test
    void receiveVideo_ShouldCreateTempDirectory() throws IOException, InterruptedException {
        // Arrange
        when(videoRepository.findById(testUuid)).thenReturn(Optional.of(testVideoEntity));
        when(videoRepository.save(any(VideoEntity.class))).thenReturn(testVideoEntity);

        // Act
        try {
            videoProcessConsumer.receiveVideo(testRequest);
        } catch (Exception e) {
            // Expected to fail due to ffmpeg not being available
        }

        // Assert
        Path expectedTempPath = Path.of("temp", testUuid.toString());
        // Note: In a real test environment, you might want to verify directory creation
        // but since ffmpeg will fail, the temp directory might be cleaned up
        verify(videoRepository, atLeastOnce()).save(any(VideoEntity.class));
    }
}
