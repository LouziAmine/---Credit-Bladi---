package com.credit.bladi.unittest.controller.creditrate.mapper;

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
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreditRateMapperTest {

    @Test
    void shouldMapPatchDtoToDomain() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();
        dto.setRateMin(new BigDecimal("3.50"));
        dto.setRateMax(new BigDecimal("5.50"));
        dto.setMinDurationMonths(12);
        dto.setMaxDurationMonths(300);
        dto.setActive(true);

        CreditRateModel model = CreditRateMapper.toDomain(1L, dto);

        assertEquals(1L, model.getId());
        assertEquals(0, model.getRateMin().value().compareTo(new BigDecimal("3.50")));
        assertEquals(0, model.getRateMax().value().compareTo(new BigDecimal("5.50")));
        assertEquals(12, model.getDurationRange().minMonths());
        assertEquals(300, model.getDurationRange().maxMonths());
        assertTrue(model.getActive());
    }

    @Test
    void shouldMapPatchDtoWithoutDurationToDomain() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();

        CreditRateModel model = CreditRateMapper.toDomain(1L, dto);

        assertNull(model.getDurationRange());
    }

    @Test
    void shouldThrowWhenOnlyMinDurationProvided() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();
        dto.setMinDurationMonths(12);

        assertThrows(CreditRateException.class, () -> CreditRateMapper.toDomain(1L, dto));
    }

    @Test
    void shouldThrowWhenOnlyMaxDurationProvided() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();
        dto.setMaxDurationMonths(300);

        assertThrows(CreditRateException.class, () -> CreditRateMapper.toDomain(1L, dto));
    }

    @Test
    void shouldMapModelToResponseDto() {
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(CspCode.SALA)
            .rateMin(Rate.of(new BigDecimal("3.50")))
            .rateMax(Rate.of(new BigDecimal("5.50")))
            .durationRange(DurationRange.of(12, 300))
            .active(true)
            .build();

        CreditRateResponseDTO dto = CreditRateMapper.toResponseDTO(model);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("SALA", dto.getCspCode());
        assertEquals(0, dto.getRateMin().compareTo(new BigDecimal("3.50")));
        assertEquals(12, dto.getMinDurationMonths());
        assertEquals(300, dto.getMaxDurationMonths());
        assertTrue(dto.getActive());
    }

    @Test
    void shouldMapModelWithoutCspCodeOrDurationRangeToResponseDto() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).build();

        CreditRateResponseDTO dto = CreditRateMapper.toResponseDTO(model);

        assertNull(dto.getCspCode());
        assertNull(dto.getMinDurationMonths());
        assertNull(dto.getMaxDurationMonths());
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
        assertEquals("SALA", result.getItems().get(0).getCspCode());
    }
}
