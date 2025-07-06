package com.hackton.soat.videoprocessor.repository;

import com.hackton.soat.videoprocessor.model.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoRepository extends JpaRepository<VideoEntity, UUID> {
}
