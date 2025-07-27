package com.hackton.soat.videoprocessor.service;

import com.hackton.soat.videoprocessor.controller.ResponseVideoDTO;
import com.hackton.soat.videoprocessor.message.pojo.VideoVO;
import com.hackton.soat.videoprocessor.message.pojo.VideoProcessRequest;
import com.hackton.soat.videoprocessor.model.VideoEntity;
import com.hackton.soat.videoprocessor.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final RabbitTemplate rabbitTemplate;
    private final VideoRepository videoRepository;

    @Value("${queue.video.process}")
    private String videoProcessQueue;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Override
    public ResponseVideoDTO uploadVideo(MultipartFile file) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        try {
            VideoVO videoUpdloaded = uploadFile(file, timestamp);
            VideoEntity videoEntity = salvar(videoUpdloaded);

            // Criar mensagem para a fila
            VideoProcessRequest videoProcessRequest = new VideoProcessRequest();
            videoProcessRequest.setId(videoEntity.getUuid().toString());
            videoProcessRequest.setVideoPath(videoEntity.getPath());
            videoProcessRequest.setTimestamp(timestamp);

            rabbitTemplate.convertAndSend(videoProcessQueue, videoProcessRequest);
            return ResponseVideoDTO.of(videoEntity);
        } catch (IOException e) {
            return ResponseVideoDTO.builder()
                    .message("Error uploading file: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseVideoDTO.builder()
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    private VideoEntity salvar(VideoVO videoUpdloaded) {
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setName(videoUpdloaded.getVideoName());
        videoEntity.setPath(videoUpdloaded.getVideoPath());
        videoEntity.setTimestamp(LocalDateTime.now());
        videoEntity.setStatus(videoUpdloaded.getStatus());
        return videoRepository.save(videoEntity);
    }

    private VideoVO uploadFile(MultipartFile file, String timestamp) throws IOException {
        try {
            //File uploads = new File(uploadDir);
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = timestamp + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return VideoVO.builder()
                    .timestamp(timestamp)
                    .videoName(fileName)
                    .videoPath(filePath.toAbsolutePath().toString())
                    .status("Uploaded")
                    .videoSize(file.getSize() + " bytes")
                    .build();

        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public StatusVideoVO obterStatus(String id) {
        VideoEntity videoEntity = videoRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
        return StatusVideoVO.of(videoEntity.getUuid().toString(), videoEntity.getStatus());
    }

    @Override
    public Resource downloadZip(String id) {
        VideoEntity videoEntity = videoRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

        if (!"CONCLUIDO".equals(videoEntity.getStatus())) {
            throw new RuntimeException("Video processing not completed yet. Current status: " + videoEntity.getStatus());
        }

        if (videoEntity.getZipPath() == null || videoEntity.getZipPath().isEmpty()) {
            throw new RuntimeException("ZIP file not available for video with id: " + id);
        }

        FileSystemResource resource = new FileSystemResource(videoEntity.getZipPath());
        if (!resource.exists()) {
            throw new RuntimeException("ZIP file not found at path: " + videoEntity.getZipPath());
        }

        return resource;
    }

}
