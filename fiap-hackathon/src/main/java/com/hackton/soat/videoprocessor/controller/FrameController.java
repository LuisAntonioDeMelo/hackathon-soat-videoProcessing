package com.hackton.soat.videoprocessor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api/frames")
@RequiredArgsConstructor
public class FrameController {

    @Value("${output.dir:outputs}")
    private String outputDir;

    @GetMapping("/{videoId}/{frameFilename}")
    public ResponseEntity<byte[]> getFrame(@PathVariable String videoId, @PathVariable String frameFilename) {
        try {
            // Primeiro, tentar localizar pasta descompactada
            Path framesPath = Paths.get(outputDir, "frames_" + videoId, frameFilename);
            File frameFile = framesPath.toFile();
            
            if (frameFile.exists()) {
                byte[] imageBytes = Files.readAllBytes(frameFile.toPath());
                String contentType = frameFilename.toLowerCase().endsWith(".png") 
                    ? MediaType.IMAGE_PNG_VALUE : MediaType.IMAGE_JPEG_VALUE;
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(imageBytes);
            }
            
            // Se não encontrar, tentar extrair do ZIP
            Path zipPath = Paths.get(outputDir, "frames_" + videoId + ".zip");
            File zipFile = zipPath.toFile();
            
            if (!zipFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equals(frameFilename) || entry.getName().endsWith("/" + frameFilename)) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            baos.write(buffer, 0, length);
                        }
                        
                        String contentType = frameFilename.toLowerCase().endsWith(".png") 
                            ? MediaType.IMAGE_PNG_VALUE : MediaType.IMAGE_JPEG_VALUE;
                        
                        return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                            .body(baos.toByteArray());
                    }
                }
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar frame: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{videoId}/list")
    public ResponseEntity<?> listFrames(@PathVariable String videoId) {
        try {
            // Primeiro, tentar pasta descompactada
            Path framesPath = Paths.get(outputDir, "frames_" + videoId);
            File framesDir = framesPath.toFile();

            if (framesDir.exists() && framesDir.isDirectory()) {
                File[] frameFiles = framesDir.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".jpg") ||
                                name.toLowerCase().endsWith(".jpeg") ||
                                name.toLowerCase().endsWith(".png"));

                if (frameFiles != null && frameFiles.length > 0) {
                    java.util.Arrays.sort(frameFiles, (a, b) -> a.getName().compareTo(b.getName()));
                    String[] frameNames = new String[frameFiles.length];
                    for (int i = 0; i < frameFiles.length; i++) {
                        frameNames[i] = frameFiles[i].getName();
                    }
                    return ResponseEntity.ok(frameNames);
                }
            }
            
            // Se não encontrar pasta, tentar ZIP
            Path zipPath = Paths.get(outputDir, "frames_" + videoId + ".zip");
            File zipFile = zipPath.toFile();
            
            if (!zipFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            List<String> frameNames = new ArrayList<>();
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    if (!entry.isDirectory() && 
                        (entryName.toLowerCase().endsWith(".jpg") ||
                         entryName.toLowerCase().endsWith(".jpeg") ||
                         entryName.toLowerCase().endsWith(".png"))) {
                        // Pegar apenas o nome do arquivo, sem o path
                        String fileName = entryName.contains("/") 
                            ? entryName.substring(entryName.lastIndexOf("/") + 1)
                            : entryName;
                        frameNames.add(fileName);
                    }
                }
            }
            
            frameNames.sort(String::compareTo);
            return ResponseEntity.ok(frameNames.toArray(new String[0]));

        } catch (Exception e) {
            System.err.println("Erro ao listar frames: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
