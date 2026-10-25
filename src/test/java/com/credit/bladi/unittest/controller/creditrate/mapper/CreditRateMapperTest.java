package com.credit.bladi.unittest.controller.creditrate.mapper;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MAX;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SALA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.creditrate.mapper.CreditRateMapper;
import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreditRateMapperTest {
    @Test
    void shouldMapPatchDtoToDomain() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(
            DECIMAL_3_50, DECIMAL_5_50, 12, 300, CODE_MAX, true);

        CreditRateModel model = CreditRateMapper.toDomain(1L, dto);

        assertEquals(1L, model.getId());
        assertEquals(0, model.getRateMin().value().compareTo(DECIMAL_3_50));
        assertEquals(0, model.getRateMax().value().compareTo(DECIMAL_5_50));
        assertEquals(RateFormula.MAX, model.getRateFormula());
        assertEquals(12, model.getDurationRange().minMonths());
        assertEquals(300, model.getDurationRange().maxMonths());
        assertTrue(model.getActive());
    }

    @Test
    void shouldMapPatchDtoWithoutDurationToDomain() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(null, null, null, null, null, null);

        CreditRateModel model = CreditRateMapper.toDomain(1L, dto);

        assertNull(model.getDurationRange());
    }

    @Test
    void shouldMapPatchDtoWithoutRateFormulaToDomain() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(null, null, null, null, null, null);

        CreditRateModel model = CreditRateMapper.toDomain(1L, dto);

        assertNull(model.getRateFormula());
    }

    @Test
    void shouldThrowWhenOnlyMinDurationProvided() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(null, null, 12, null, null, null);

        assertThrows(CreditRateException.class, () -> CreditRateMapper.toDomain(1L, dto));
    }

    @Test
    void shouldThrowWhenOnlyMaxDurationProvided() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(null, null, null, 300, null, null);

        assertThrows(CreditRateException.class, () -> CreditRateMapper.toDomain(1L, dto));
    }

    @Test
    void shouldMapModelToResponseDto() {
        UUID publicId = UUID.randomUUID();
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L)
            .publicId(publicId)
            .cspCode(CspCode.SALA)
            .rateMin(Rate.of(DECIMAL_3_50))
            .rateMax(Rate.of(DECIMAL_5_50))
            .rateFormula(RateFormula.MAX)
            .durationRange(DurationRange.of(12, 300))
            .active(true)
            .build();

        CreditRateResponseDTO dto = CreditRateMapper.toResponseDTO(model);

        assertNotNull(dto);
        assertEquals(publicId, dto.id());
        assertEquals(CODE_SALA, dto.cspCode());
        assertEquals(0, dto.rateMin().compareTo(DECIMAL_3_50));
        assertEquals(CODE_MAX, dto.rateFormula());
        assertEquals(12, dto.minDurationMonths());
        assertEquals(300, dto.maxDurationMonths());
        assertTrue(dto.active());
    }

    @Test
    void shouldMapModelWithoutCspCodeOrDurationRangeToResponseDto() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).build();

        CreditRateResponseDTO dto = CreditRateMapper.toResponseDTO(model);

        assertNull(dto.cspCode());
        assertNull(dto.minDurationMonths());
        assertNull(dto.maxDurationMonths());
    }

    @Test
    void shouldReturnNullResponseDtoWhenModelIsNull() {
        assertNull(CreditRateMapper.toResponseDTO(null));
    }

    @Test
    void shouldMapCreditPageOfModelsToCreditPageOfDtos() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).cspCode(CspCode.SALA).build();
        CreditPage<CreditRateModel> source = new CreditPage<>();
        source.setItems(List.of(model));
        source.setCurrentPage(1);
        source.setLastPage(1);
        source.setSize(20);
        source.setTotal(1);

        CreditPage<CreditRateResponseDTO> result = CreditRateMapper.toCreditPage(source);

        assertEquals(1, result.getItems().size());
        assertEquals(CODE_SALA, result.getItems().get(0).cspCode());
    }
}
