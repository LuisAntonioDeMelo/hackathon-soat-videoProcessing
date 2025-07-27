package com.hackton.soat.videoprocessor.service;

import com.hackton.soat.videoprocessor.controller.ResponseVideoDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    ResponseVideoDTO uploadVideo(MultipartFile file);

    StatusVideoVO obterStatus(String id);

    Resource downloadZip(String id);
}
