package com.credit.bladi.unittest.controller.mourabaha.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO;
import com.credit.bladi.controller.dto.mourabaha.response.MourabahaParametersResponseDTO;
import com.credit.bladi.controller.mourabaha.mapper.MourabahaParametersMapper;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class MourabahaParametersMapperTest {

    @Test
    void shouldMapPatchDtoToDomain() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO();
        dto.setProfitRate(new BigDecimal("5.25"));
        dto.setVatRate(new BigDecimal("0.10"));
        dto.setActive(true);

        MourabahaParametersModel model = MourabahaParametersMapper.toDomain(1L, dto);

        assertEquals(1L, model.getId());
        assertEquals(0, model.getProfitRate().value().compareTo(new BigDecimal("5.25")));
        assertEquals(0, model.getVatRate().value().compareTo(new BigDecimal("0.10")));
        assertTrue(model.getActive());
    }

    @Test
    void shouldMapPatchDtoWithNullFieldsToDomain() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO();

        MourabahaParametersModel model = MourabahaParametersMapper.toDomain(1L, dto);

        assertEquals(1L, model.getId());
        assertNull(model.getProfitRate());
        assertNull(model.getVatRate());
        assertNull(model.getActive());
    }

    @Test
    void shouldMapModelToResponseDto() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.25")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();

        MourabahaParametersResponseDTO dto = MourabahaParametersMapper.toResponseDTO(model);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(0, dto.getProfitRate().compareTo(new BigDecimal("5.25")));
        assertEquals(0, dto.getVatRate().compareTo(new BigDecimal("0.10")));
        assertTrue(dto.getActive());
    }

    @Test
    void shouldMapModelWithNullRatesToResponseDto() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();

        MourabahaParametersResponseDTO dto = MourabahaParametersMapper.toResponseDTO(model);

        assertNotNull(dto);
        assertNull(dto.getProfitRate());
        assertNull(dto.getVatRate());
    }

    @Test
    void shouldReturnNullResponseDtoWhenModelIsNull() {
        assertNull(MourabahaParametersMapper.toResponseDTO(null));
    }

    @Test
    void shouldMapCreditPageOfModelsToCreditPageOfDtos() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.25")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();
        CreditPage<MourabahaParametersModel> source = new CreditPage<>();
        source.setItems(List.of(model));
        source.setCurrentPage(1);
        source.setLastPage(1);
        source.setSize(20);
        source.setTotal(1);

        CreditPage<MourabahaParametersResponseDTO> result = MourabahaParametersMapper.toCreditPage(source);

        assertEquals(1, result.getItems().size());
        assertEquals(1L, result.getItems().get(0).getId());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getLastPage());
        assertEquals(20, result.getSize());
        assertEquals(1, result.getTotal());
    }
}
