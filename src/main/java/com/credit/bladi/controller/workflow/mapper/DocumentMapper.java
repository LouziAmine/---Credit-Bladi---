package com.credit.bladi.controller.workflow.mapper;

import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DownloadableDocument;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class DocumentMapper {
    private DocumentMapper() {
    }

    public static DemandDocumentResponseDTO toResponseDTO(DemandDocumentModel model) {
        return new DemandDocumentResponseDTO(
            model.publicId(),
            model.stepCode(),
            model.stepName(),
            model.originalFilename(),
            model.contentType(),
            model.sizeBytes(),
            model.checksumSha256(),
            model.documentType(),
            model.status().name(),
            model.scanStatus().name(),
            model.supersededByPublicId(),
            model.uploadedBy(),
            model.uploadedAt());
    }

    public static ResponseEntity<Resource> toDownloadResponse(DownloadableDocument downloadable) {
        Resource resource = new ByteArrayResource(downloadable.content());
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(downloadable.document().contentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename(downloadable.document().originalFilename(), StandardCharsets.UTF_8)
                    .build()
                    .toString())
            .body(resource);
    }
}
