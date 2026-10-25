package com.credit.bladi.unittest.controller.workflow.mapper;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ACTIVE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLEAN;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SUPERSEDED;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_01_01T10_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_A1B2C3_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_OLD_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_PIECE_IDENTITE_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_PDF;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PIECE_D_IDENTITE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
import com.credit.bladi.controller.workflow.mapper.DocumentMapper;
import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.ScanStatus;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DocumentMapperTest {
    @Test
    void shouldMapActiveDocumentWithoutSupersededBy() {
        UUID publicId = UUID.randomUUID();
        DemandDocumentModel model = new DemandDocumentModel(1L, publicId, 10L, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, FILE_PIECE_IDENTITE_PDF, FILE_A1B2C3_PDF, MIME_APPLICATION_PDF,
            245678L,
            "e3b0c4", LABEL_PIECE_D_IDENTITE, DocumentStatus.ACTIVE, ScanStatus.CLEAN, null, null, USER_MANAGER1,
            Instant.parse(DATE_2026_01_01T10_00_00Z));

        DemandDocumentResponseDTO dto = DocumentMapper.toResponseDTO(model);

        assertEquals(publicId, dto.id());
        assertEquals(CODE_RISK_ANALYSIS_STEP, dto.stepCode());
        assertEquals(FILE_PIECE_IDENTITE_PDF, dto.originalFilename());
        assertEquals(MIME_APPLICATION_PDF, dto.contentType());
        assertEquals(245678L, dto.sizeBytes());
        assertEquals("e3b0c4", dto.checksumSha256());
        assertEquals(LABEL_PIECE_D_IDENTITE, dto.documentType());
        assertEquals(CODE_ACTIVE, dto.status());
        assertEquals(CODE_CLEAN, dto.scanStatus());
        assertNull(dto.supersededByDocumentId());
        assertEquals(USER_MANAGER1, dto.uploadedBy());
    }

    @Test
    void shouldMapSupersededDocumentWithReplacementId() {
        UUID replacementPublicId = UUID.randomUUID();
        DemandDocumentModel model = new DemandDocumentModel(1L, UUID.randomUUID(), 10L, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE, FILE_OLD_PDF, FILE_A1B2C3_PDF, MIME_APPLICATION_PDF, 100L, "checksum",
            LABEL_PIECE_D_IDENTITE,
            DocumentStatus.SUPERSEDED, ScanStatus.CLEAN, 5L, replacementPublicId, USER_MANAGER1, Instant.now());

        DemandDocumentResponseDTO dto = DocumentMapper.toResponseDTO(model);

        assertEquals(CODE_SUPERSEDED, dto.status());
        assertEquals(replacementPublicId, dto.supersededByDocumentId());
    }
}
