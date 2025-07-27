package com.hackton.soat.videoprocessor.service;

import com.hackton.soat.videoprocessor.controller.ResponseVideoDTO;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoServiceImpl videoService;

    @TempDir
    Path tempDir;

    private UUID testUuid;
    private VideoEntity testVideoEntity;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(videoService, "videoProcessQueue", "testQueue");
        testUuid = UUID.randomUUID();
        testVideoEntity = new VideoEntity();
        testVideoEntity.setUuid(testUuid);
        testVideoEntity.setName("test-video.mp4");
        testVideoEntity.setPath("/path/to/video.mp4");
        testVideoEntity.setStatus("Uploaded");
        testVideoEntity.setTimestamp(LocalDateTime.now());
    }

    @Test
    void uploadVideo_WithValidFile_ShouldReturnSuccessResponse() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test-video.mp4", 
                "video/mp4", 
                "test video content".getBytes()
        );

        when(videoRepository.save(any(VideoEntity.class))).thenReturn(testVideoEntity);

        // Act
        ResponseVideoDTO result = videoService.uploadVideo(file);

        // Assert
        assertNotNull(result);
        assertEquals("Video processed successfully", result.getMessage());
        assertEquals(testUuid.toString(), result.getId());
        assertEquals("test-video.mp4", result.getVideoName());

        // Verify RabbitMQ message was sent
        ArgumentCaptor<VideoProcessRequest> messageCaptor = ArgumentCaptor.forClass(VideoProcessRequest.class);
        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate).convertAndSend(queueCaptor.capture(), messageCaptor.capture());

        assertEquals("testQueue", queueCaptor.getValue());
        VideoProcessRequest sentMessage = messageCaptor.getValue();
        assertEquals(testUuid.toString(), sentMessage.getId());
        assertNotNull(sentMessage.getVideoPath());
        assertNotNull(sentMessage.getTimestamp());
    }

    @Test
    void uploadVideo_WithEmptyFile_ShouldReturnErrorResponse() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.mp4",
                "video/mp4",
                new byte[0]
        );

        // Act
        ResponseVideoDTO result = videoService.uploadVideo(emptyFile);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMessage().contains("An unexpected error occurred") ||
                   result.getMessage().contains("Error uploading file"));
        verify(videoRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(VideoProcessRequest.class));
    }

    @Test
    void obterStatus_WithValidId_ShouldReturnStatus() {
        // Arrange
        when(videoRepository.findById(testUuid)).thenReturn(Optional.of(testVideoEntity));

        // Act
        StatusVideoVO result = videoService.obterStatus(testUuid.toString());

        // Assert
        assertNotNull(result);
        assertEquals(testUuid.toString(), result.getId());
        assertEquals("Uploaded", result.getStatus());
    }

    @Test
    void obterStatus_WithInvalidId_ShouldThrowException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(videoRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> videoService.obterStatus(invalidId.toString()));
        
        assertTrue(exception.getMessage().contains("Video not found with id"));
    }

    @Test
    void obterStatus_WithInvalidUUIDFormat_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> videoService.obterStatus("invalid-uuid-format"));
    }

    @Test
    void downloadZip_WithValidCompletedVideo_ShouldReturnResource() {
        // Arrange
        testVideoEntity.setStatus("CONCLUIDO");
        testVideoEntity.setZipPath("outputs/frames_" + testUuid + ".zip");
        when(videoRepository.findById(testUuid)).thenReturn(Optional.of(testVideoEntity));

        // Act
        Resource result = videoService.downloadZip(testUuid.toString());

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof FileSystemResource);
    }

    @Test
    void downloadZip_WithInvalidId_ShouldThrowException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(videoRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> videoService.downloadZip(invalidId.toString()));

        assertTrue(exception.getMessage().contains("Video not found with id"));
    }

    @Test
    void downloadZip_WithIncompleteProcessing_ShouldThrowException() {
        // Arrange
        testVideoEntity.setStatus("processando");
        when(videoRepository.findById(testUuid)).thenReturn(Optional.of(testVideoEntity));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> videoService.downloadZip(testUuid.toString()));

        assertTrue(exception.getMessage().contains("Video processing not completed yet"));
    }

    @Test
    void downloadZip_WithNullZipPath_ShouldThrowException() {
        // Arrange
        testVideoEntity.setStatus("CONCLUIDO");
        testVideoEntity.setZipPath(null);
        when(videoRepository.findById(testUuid)).thenReturn(Optional.of(testVideoEntity));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> videoService.downloadZip(testUuid.toString()));

        assertTrue(exception.getMessage().contains("ZIP file not available"));
    }
}
