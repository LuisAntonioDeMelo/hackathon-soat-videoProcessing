package com.hackton.soat.videoprocessor.service;

import com.hackton.soat.videoprocessor.controller.ResponseVideoDTO;
import com.hackton.soat.videoprocessor.message.pojo.VideoVO;
import com.hackton.soat.videoprocessor.model.VideoEntity;
import com.hackton.soat.videoprocessor.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final RabbitTemplate rabbitTemplate;
    private final VideoRepository videoRepository;

    @Override
    public ResponseVideoDTO uploadVideo(MultipartFile file) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            VideoVO videoUpdloaded = uploadFile(file, timestamp);
            VideoEntity videoEntity = salvar(videoUpdloaded);
            rabbitTemplate.convertAndSend("videoQueue", videoEntity);
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
            File uploads = new File("uploads");
            uploads.mkdirs();
            String fileName = timestamp + "_" + file.getOriginalFilename();
            File fileNew = new File(uploads, fileName);
            file.transferTo(fileNew);
            return VideoVO.builder()
                    .timestamp(timestamp)
                    .videoName(fileName)
                    .videoPath(fileNew.getAbsolutePath())
                    .status("Uploaded")
                    .videoSize(file.getSize() + " bytes")
                    .build();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Optional<Object> status(String id) {
        return Optional.empty();
    }

}
