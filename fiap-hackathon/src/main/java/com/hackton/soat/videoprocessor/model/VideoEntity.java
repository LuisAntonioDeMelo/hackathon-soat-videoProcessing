package com.hackton.soat.videoprocessor.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private String name;
    private String path;
    private LocalDateTime timestamp;
    private String status;
    private String zipPath;
    private Integer frameCount;

    @Lob
    private String imagesJson;
}
