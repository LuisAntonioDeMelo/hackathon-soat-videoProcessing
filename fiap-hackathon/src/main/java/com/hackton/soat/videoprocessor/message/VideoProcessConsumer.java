package com.hackton.soat.videoprocessor.message;

import com.hackton.soat.videoprocessor.message.pojo.VideoProcessRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Log4j2
@Component
public class VideoProcessConsumer {

    public void receiveVideo(VideoProcessRequest videoProcessRequest){
        log.info("Iniciar processamento de Video: {}", videoProcessRequest.getVideoPath());
        Path videoPath = Path.of(videoProcessRequest.getVideoPath());
    }

}
