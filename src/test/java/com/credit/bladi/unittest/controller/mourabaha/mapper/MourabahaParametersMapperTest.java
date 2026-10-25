package com.credit.bladi.unittest.controller.mourabaha.mapper;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MourabahaParametersMapperTest {
    @Test
    void shouldMapPatchDtoToDomain() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO(
            DECIMAL_5_25, DECIMAL_0_10, DECIMAL_0_0004, true);

        MourabahaParametersModel model = MourabahaParametersMapper.toDomain(1L, dto);

        assertEquals(1L, model.getId());
        assertEquals(0, model.getProfitRate().value().compareTo(DECIMAL_5_25));
        assertEquals(0, model.getVatRate().value().compareTo(DECIMAL_0_10));
        assertEquals(0, model.getTakafulMonthlyRate().value().compareTo(DECIMAL_0_0004));
        assertTrue(model.getActive());
    }

    @Test
    void shouldMapPatchDtoWithNullFieldsToDomain() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO(null, null, null, null);

        MourabahaParametersModel model = MourabahaParametersMapper.toDomain(1L, dto);

        assertEquals(1L, model.getId());
        assertNull(model.getProfitRate());
        assertNull(model.getVatRate());
        assertNull(model.getTakafulMonthlyRate());
        assertNull(model.getActive());
    }

    @Test
    void shouldMapModelToResponseDto() {
        UUID publicId = UUID.randomUUID();
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder()
            .id(1L)
            .publicId(publicId)
            .profitRate(Rate.of(DECIMAL_5_25))
            .vatRate(Ratio.of(DECIMAL_0_10))
            .takafulMonthlyRate(Rate.of(DECIMAL_0_0004))
            .active(true)
            .build();

        MourabahaParametersResponseDTO dto = MourabahaParametersMapper.toResponseDTO(model);

        assertNotNull(dto);
        assertEquals(publicId, dto.id());
        assertEquals(0, dto.profitRate().compareTo(DECIMAL_5_25));
        assertEquals(0, dto.vatRate().compareTo(DECIMAL_0_10));
        assertEquals(0, dto.takafulMonthlyRate().compareTo(DECIMAL_0_0004));
        assertTrue(dto.active());
    }

    @Test
    void shouldMapModelWithNullRatesToResponseDto() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();

        MourabahaParametersResponseDTO dto = MourabahaParametersMapper.toResponseDTO(model);

        assertNotNull(dto);
        assertNull(dto.profitRate());
        assertNull(dto.vatRate());
    }

    @Test
    void shouldReturnNullResponseDtoWhenModelIsNull() {
        assertNull(MourabahaParametersMapper.toResponseDTO(null));
    }

    @Test
    void shouldMapCreditPageOfModelsToCreditPageOfDtos() {
        UUID publicId = UUID.randomUUID();
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder()
            .id(1L)
            .publicId(publicId)
            .profitRate(Rate.of(DECIMAL_5_25))
            .vatRate(Ratio.of(DECIMAL_0_10))
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
        assertEquals(publicId, result.getItems().get(0).id());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getLastPage());
        assertEquals(20, result.getSize());
        assertEquals(1, result.getTotal());
    }
}
