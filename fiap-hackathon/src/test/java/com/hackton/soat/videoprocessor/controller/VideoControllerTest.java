package com.hackton.soat.videoprocessor.controller;

import com.hackton.soat.videoprocessor.service.StatusVideoVO;
import com.hackton.soat.videoprocessor.service.VideoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(videoController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void uploadVideo_WithValidFile_ShouldReturnSuccess() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test-video.mp4", 
                "video/mp4", 
                "test video content".getBytes()
        );
        
        ResponseVideoDTO responseVideoDTO = ResponseVideoDTO.builder()
                .message("Video processed successfully")
                .id("123e4567-e89b-12d3-a456-426614174000")
                .videoName("test-video.mp4")
                .build();

        when(videoService.uploadVideo(any())).thenReturn(responseVideoDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/videos/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Video processed successfully"))
                .andExpect(jsonPath("$.id").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.videoName").value("test-video.mp4"));
    }

    @Test
    void uploadVideo_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.mp4",
                "video/mp4",
                new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/videos/upload")
                .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("File is empty"));
    }

    @Test
    void status_WithValidId_ShouldReturnStatus() throws Exception {
        // Arrange
        String videoId = "123e4567-e89b-12d3-a456-426614174000";
        StatusVideoVO statusVideoVO = StatusVideoVO.builder()
                .id(videoId)
                .status("CONCLUIDO")
                .build();

        when(videoService.obterStatus(anyString())).thenReturn(statusVideoVO);

        // Act & Assert
        mockMvc.perform(get("/api/videos/status/{id}", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(videoId))
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));
    }

    @Test
    void status_WithInvalidId_ShouldReturnError() throws Exception {
        // Arrange
        String invalidId = "invalid-id";
        when(videoService.obterStatus(anyString()))
                .thenThrow(new RuntimeException("Video not found with id: " + invalidId));

        // Act & Assert
        mockMvc.perform(get("/api/videos/status/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadZip_WithValidId_ShouldReturnZipFile() throws Exception {
        // Arrange
        String videoId = "123e4567-e89b-12d3-a456-426614174000";
        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("test content".getBytes()));

        when(videoService.downloadZip(anyString())).thenReturn(mockResource);

        // Act & Assert
        mockMvc.perform(get("/api/videos/download/{id}", videoId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/octet-stream"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"frames_" + videoId + ".zip\""));
    }

    @Test
    void downloadZip_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange
        String invalidId = "invalid-id";
        when(videoService.downloadZip(anyString()))
                .thenThrow(new RuntimeException("Video not found with id: " + invalidId));

        // Act & Assert
        mockMvc.perform(get("/api/videos/download/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadZip_WithIncompleteProcessing_ShouldReturnNotFound() throws Exception {
        // Arrange
        String videoId = "123e4567-e89b-12d3-a456-426614174000";
        when(videoService.downloadZip(anyString()))
                .thenThrow(new RuntimeException("Video processing not completed yet. Current status: processando"));

        // Act & Assert
        mockMvc.perform(get("/api/videos/download/{id}", videoId))
                .andExpect(status().isNotFound());
    }
}
