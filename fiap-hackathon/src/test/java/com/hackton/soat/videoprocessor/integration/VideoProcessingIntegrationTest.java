package com.hackton.soat.videoprocessor.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackton.soat.videoprocessor.controller.ResponseVideoDTO;
import com.hackton.soat.videoprocessor.model.VideoEntity;
import com.hackton.soat.videoprocessor.repository.VideoRepository;
import com.hackton.soat.videoprocessor.service.StatusVideoVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class VideoProcessingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullVideoProcessingFlow_ShouldWorkCorrectly() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        // Act 1: Upload video
        MvcResult uploadResult = mockMvc.perform(multipart("/api/videos/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Video processed successfully"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Verify upload response
        String uploadResponseJson = uploadResult.getResponse().getContentAsString();
        ResponseVideoDTO uploadResponse = objectMapper.readValue(uploadResponseJson, ResponseVideoDTO.class);
        assertNotNull(uploadResponse);
        assertNotNull(uploadResponse.getId());

        // Act 2: Check status
        mockMvc.perform(get("/api/videos/status/{id}", uploadResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uploadResponse.getId()))
                .andExpect(jsonPath("$.status").value("Uploaded"));
    }

    @Test
    void uploadVideo_WithInvalidFile_ShouldReturnBadRequest() throws Exception {
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
                .andExpect(content().string("File is empty"));
    }

    @Test
    void getStatus_WithNonExistentId_ShouldReturnError() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/videos/status/{id}", nonExistentId.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getStatus_WithInvalidUUIDFormat_ShouldReturnError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/videos/status/{id}", "invalid-uuid"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void videoStatusProgression_ShouldWorkCorrectly() throws Exception {
        // Create a video first
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/videos/upload")
                .file(file))
                .andExpect(status().isOk())
                .andReturn();

        String uploadResponseJson = uploadResult.getResponse().getContentAsString();
        ResponseVideoDTO uploadResponse = objectMapper.readValue(uploadResponseJson, ResponseVideoDTO.class);
        String videoId = uploadResponse.getId();

        // Test status retrieval
        mockMvc.perform(get("/api/videos/status/{id}", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(videoId))
                .andExpect(jsonPath("$.status").value("Uploaded"));
    }
}
