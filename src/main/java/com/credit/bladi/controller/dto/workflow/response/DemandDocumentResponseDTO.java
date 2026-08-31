package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "DemandDocumentResponseDTO", description = "A file uploaded against a demand's processing "
    + "pipeline. Immutable once created: a re-upload of the same documentType supersedes this row instead "
    + "of replacing it, so the full document history stays on record.")
public record DemandDocumentResponseDTO(
    @Schema(description = "Document identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Code of the step the demand was on when this document was uploaded.",
        example = "RISK_ANALYSIS_STEP") String stepCode,
    @Schema(description = "Name of the step the demand was on when this document was uploaded.",
        example = "Credit file counter-analysis") String stepName,
    @Schema(description = "Original filename as supplied by the uploader.", example = "id-document.pdf")
    String originalFilename,
    @Schema(description = "Detected content type (from the file's content, not the client-supplied header).",
        example = "application/pdf") String contentType,
    @Schema(description = "File size in bytes.", example = "245678") Long sizeBytes,
    @Schema(description = "SHA-256 checksum of the file content, for independent integrity verification.",
        example = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855") String checksumSha256,
    @Schema(description = "Optional free-text tag for the kind of document.", example = "ID document")
    String documentType,
    @Schema(description = "ACTIVE (current) or SUPERSEDED (replaced by a later upload of the same "
        + "documentType, kept for history).", example = "ACTIVE") String status,
    @Schema(description = "Antivirus scan verdict. Download is only allowed when CLEAN or SKIPPED.",
        example = "CLEAN") String scanStatus,
    @Schema(description = "If SUPERSEDED, the public id of the document that replaced this one.",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    UUID supersededByDocumentId,
    @Schema(description = "Username of the uploader.", example = "manager1") String uploadedBy,
    @Schema(description = "When the document was uploaded.") Instant uploadedAt
) {}
