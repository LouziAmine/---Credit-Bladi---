package com.credit.bladi.controller.workflow.mapper;

import com.credit.bladi.model.workflow.UploadedFile;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.web.multipart.MultipartFile;

public final class UploadedFileMapper {
    private UploadedFileMapper() {
    }

    public static UploadedFile toUploadedFile(MultipartFile file) {
        try {
            return new UploadedFile(file.getOriginalFilename(), file.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file content", e);
        }
    }
}
