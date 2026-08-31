package com.credit.bladi.unittest.controller.bam.mapper;

import static com.credit.bladi.unittest.mock.bam.MockBamParameters.BAM_ID;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.PAGE;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.PATCH_PROCESSING_FEES_RATIO;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.SIZE;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.TOTAL;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersPage;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersResponseDTO;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.patchRequest;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.bam.mapper.BamParametersMapper;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Ratio;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

class BamParametersMapperTest {
    @Test
    void shouldMapPatchDTOToDomain() {
        BamParametersModel domain = BamParametersMapper.toDomain(BAM_ID, patchRequest());

        BamParametersModel expected = BamParametersModel.getBuilder()
            .id(BAM_ID)
            .processingFeesRatio(Ratio.of(PATCH_PROCESSING_FEES_RATIO))
            .build();

        assertTrue(EqualsBuilder.reflectionEquals(expected, domain));
    }

    @Test
    void shouldMapPageToResponseDTOPage() {
        CreditPage<BamParametersResponseDTO> result = BamParametersMapper.toCreditPage(bamParametersPage());

        assertNotNull(result);
        assertEquals(TOTAL, result.getTotal());
        assertEquals(PAGE, result.getCurrentPage());
        assertEquals(SIZE, result.getSize());
        assertEquals(1, result.getItems().size());
        assertEquals(bamParametersResponseDTO(), result.getItems().getFirst());
    }

    @Test
    void shouldReturnEmptyPageWhenSourceIsNull() {
        CreditPage<BamParametersResponseDTO> result = BamParametersMapper.toCreditPage(null);

        assertNotNull(result);
        assertEquals(0, result.getItems().size());
    }

    @Test
    void shouldReturnNullResponseDtoWhenModelIsNull() {
        assertNull(BamParametersMapper.toResponseDTO(null));
    }

    @Test
    void shouldMapPatchDtoWithMoneyFieldsToDomain() {
        com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO dto =
            new com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO(
                null, AMOUNT_1000, AMOUNT_10000,
                null, null, null, null, null, null, null, null);

        BamParametersModel model = BamParametersMapper.toDomain(BAM_ID, dto);

        assertEquals(0, model.getMinProcessingFees().amount().compareTo(AMOUNT_1000));
        assertEquals(0, model.getMaxProcessingFees().amount().compareTo(AMOUNT_10000));
    }

    @Test
    void shouldMapModelWithNullFieldsToResponseDto() {
        BamParametersModel model = BamParametersModel.getBuilder().id(BAM_ID).build();

        BamParametersResponseDTO dto = BamParametersMapper.toResponseDTO(model);

        assertNull(dto.processingFeesRatio());
        assertNull(dto.minProcessingFees());
    }
}
