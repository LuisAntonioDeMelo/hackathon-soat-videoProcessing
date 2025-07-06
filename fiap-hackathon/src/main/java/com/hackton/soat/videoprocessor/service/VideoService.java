package com.hackton.soat.videoprocessor.service;

import com.hackton.soat.videoprocessor.controller.ResponseVideoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface VideoService {

    ResponseVideoDTO uploadVideo(MultipartFile file);

    Optional<Object> status(String id);
}
