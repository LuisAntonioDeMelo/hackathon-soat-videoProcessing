package com.hackton.soat.videoprocessor.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusVideoVOTest {

    @Test
    void of_WithValidParameters_ShouldCreateCorrectVO() {
        // Arrange
        String testId = "123e4567-e89b-12d3-a456-426614174000";
        String testStatus = "CONCLUIDO";

        // Act
        StatusVideoVO result = StatusVideoVO.of(testId, testStatus);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(testStatus, result.getStatus());
    }

    @Test
    void of_WithNullParameters_ShouldCreateVOWithNullValues() {
        // Act
        StatusVideoVO result = StatusVideoVO.of(null, null);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getStatus());
    }

    @Test
    void builder_ShouldCreateCorrectVO() {
        // Act
        StatusVideoVO result = StatusVideoVO.builder()
                .id("test-id")
                .status("PROCESSANDO")
                .build();

        // Assert
        assertNotNull(result);
        assertEquals("test-id", result.getId());
        assertEquals("PROCESSANDO", result.getStatus());
    }

    @Test
    void of_WithDifferentStatuses_ShouldWorkCorrectly() {
        // Test different status values
        String[] statuses = {"Uploaded", "processando", "CONCLUIDO", "erro"};
        String testId = "test-id";

        for (String status : statuses) {
            // Act
            StatusVideoVO result = StatusVideoVO.of(testId, status);

            // Assert
            assertNotNull(result);
            assertEquals(testId, result.getId());
            assertEquals(status, result.getStatus());
        }
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Arrange
        StatusVideoVO vo1 = StatusVideoVO.of("id1", "status1");
        StatusVideoVO vo2 = StatusVideoVO.of("id1", "status1");
        StatusVideoVO vo3 = StatusVideoVO.of("id2", "status1");

        // Assert
        assertEquals(vo1, vo2);
        assertNotEquals(vo1, vo3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // Arrange
        StatusVideoVO vo1 = StatusVideoVO.of("id1", "status1");
        StatusVideoVO vo2 = StatusVideoVO.of("id1", "status1");

        // Assert
        assertEquals(vo1.hashCode(), vo2.hashCode());
    }
}
