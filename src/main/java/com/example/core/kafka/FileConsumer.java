package com.example.core.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "file-consumers")
    public void consumeFile(byte[] fileBytes, @Header(KafkaHeaders.RECEIVED_KEY) String fileName) {
        try {
            Path output = Paths.get("uploads/" + fileName);
            Files.createDirectories(output.getParent());
            Files.write(output, fileBytes);
            System.out.println("✅ File saved: " + output.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
