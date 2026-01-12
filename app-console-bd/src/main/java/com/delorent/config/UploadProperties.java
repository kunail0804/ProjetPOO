package com.delorent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class UploadProperties {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public Path getUploadRoot() {
        return Path.of(uploadDir).toAbsolutePath().normalize();
    }
}