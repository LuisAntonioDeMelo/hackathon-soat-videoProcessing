package com.hackton.soat.videoprocessor.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackton.soat.videoprocessor.message.pojo.VideoProcessRequest;
import com.hackton.soat.videoprocessor.model.VideoEntity;
import com.hackton.soat.videoprocessor.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@Component
@RequiredArgsConstructor
public class VideoProcessConsumer {

    private final VideoRepository videoRepository;

    @RabbitListener(queues = "${queue.video.process}")
    public void receiveVideo(VideoProcessRequest videoProcessRequest) throws IOException, InterruptedException {
        log.info("Iniciar processamento de Video: {}", videoProcessRequest.getVideoPath());
        VideoEntity videoEntity = videoRepository.findById(UUID.fromString(videoProcessRequest.getId())).orElseThrow(
                () -> new RuntimeException("Video not found with id: " + videoProcessRequest.getId())
        );
        videoEntity.setStatus("processando");
        videoRepository.save(videoEntity);

        Path caminhoTemp = Paths.get("temp", videoProcessRequest.getId());
        Files.createDirectories(caminhoTemp);
        String pattern = caminhoTemp.resolve("frame_%04d.png").toString();
        ProcessBuilder processBuilder =  new ProcessBuilder("ffmpeg", "-i", videoProcessRequest.getVideoPath(), "-vf", "fps=1", "-y", pattern);
        var process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        if(exitCode != 0) {
            log.error("Erro ao processar video: {}", output);
            videoEntity.setStatus("erro");
            videoEntity.setZipPath(null);
            videoRepository.save(videoEntity);
            return;
        }

        log.info("Frames extraídos com sucesso: {}", output);

        List<File> frames = Arrays.asList(Objects.requireNonNull(caminhoTemp.toFile().listFiles((d, n) -> n.endsWith(".png"))));
        Path outputs = Paths.get("outputs"); Files.createDirectories(outputs);
        String zipName = "frames_" + videoEntity.getUuid().toString() + ".zip";
        Path zipPath = outputs.resolve(zipName);

        try(var zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for(File img: frames) {
                zos.putNextEntry(new ZipEntry(img.getName()));
                Files.copy(img.toPath(), zos);
                zos.closeEntry();
            }
        }

        videoEntity.setFrameCount(frames.size());
        videoEntity.setImagesJson(new ObjectMapper().writeValueAsString(
                frames.stream().map(File::getName).toList()));
        videoEntity.setZipPath(zipPath.toString());
        videoEntity.setStatus("CONCLUIDO");
        videoRepository.save(videoEntity);
        FileSystemUtils.deleteRecursively(caminhoTemp);

        log.info("Processamento de video concluído: {}", videoEntity.getUuid());
    }

}
