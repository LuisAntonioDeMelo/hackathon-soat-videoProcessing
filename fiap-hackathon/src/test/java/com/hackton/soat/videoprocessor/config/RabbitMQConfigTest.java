package com.hackton.soat.videoprocessor.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

    @InjectMocks
    private RabbitMQConfig rabbitMQConfig;

    @Test
    void videoProcessQueue_ShouldCreateDurableQueue() {
        // Arrange
        ReflectionTestUtils.setField(rabbitMQConfig, "videoProcessQueue", "testVideoQueue");

        // Act
        Queue queue = rabbitMQConfig.videoProcessQueue();

        // Assert
        assertNotNull(queue);
        assertEquals("testVideoQueue", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void videoProcessQueue_WithDifferentQueueName_ShouldCreateCorrectQueue() {
        // Arrange
        String customQueueName = "customVideoProcessQueue";
        ReflectionTestUtils.setField(rabbitMQConfig, "videoProcessQueue", customQueueName);

        // Act
        Queue queue = rabbitMQConfig.videoProcessQueue();

        // Assert
        assertNotNull(queue);
        assertEquals(customQueueName, queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void videoProcessQueue_ShouldBeRepeatable() {
        // Arrange
        ReflectionTestUtils.setField(rabbitMQConfig, "videoProcessQueue", "repeatableQueue");

        // Act
        Queue queue1 = rabbitMQConfig.videoProcessQueue();
        Queue queue2 = rabbitMQConfig.videoProcessQueue();

        // Assert
        assertNotNull(queue1);
        assertNotNull(queue2);
        assertEquals(queue1.getName(), queue2.getName());
        assertEquals(queue1.isDurable(), queue2.isDurable());
    }
}
