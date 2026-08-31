package com.credit.bladi.unittest.datasource.mourabaha.mapper;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
import com.credit.bladi.datasource.mourabaha.mapper.MourabahaParametersEntityMapper;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.util.List;
import org.junit.jupiter.api.Test;

class MourabahaParametersEntityMapperTest {
    private static MourabahaParametersEntity entity() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);
        entity.setProfitRate(DECIMAL_5_25);
        entity.setVatRate(DECIMAL_0_10);
        entity.setActive(true);
        return entity;
    }

    @Test
    void shouldMapEntityToModel() {
        MourabahaParametersModel model = MourabahaParametersEntityMapper.mapFromEntity(entity());

        assertEquals(1L, model.getId());
        assertEquals(0, model.getProfitRate().value().compareTo(DECIMAL_5_25));
        assertEquals(0, model.getVatRate().value().compareTo(DECIMAL_0_10));
        assertTrue(model.getActive());
    }

    @Test
    void shouldMapEntityWithNullRatesToModel() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);

        MourabahaParametersModel model = MourabahaParametersEntityMapper.mapFromEntity(entity);

        assertNull(model.getProfitRate());
        assertNull(model.getVatRate());
    }

    @Test
    void shouldMapModelToEntity() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(DECIMAL_5_25))
            .vatRate(Ratio.of(DECIMAL_0_10))
            .active(true)
            .build();

        MourabahaParametersEntity entity = MourabahaParametersEntityMapper.mapToEntity(model);

        assertEquals(1L, entity.getId());
        assertEquals(0, entity.getProfitRate().compareTo(DECIMAL_5_25));
        assertEquals(0, entity.getVatRate().compareTo(DECIMAL_0_10));
        assertTrue(entity.getActive());
    }

    @Test
    void shouldMapModelWithNullRatesToEntity() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();

        MourabahaParametersEntity entity = MourabahaParametersEntityMapper.mapToEntity(model);

        assertNull(entity.getProfitRate());
        assertNull(entity.getVatRate());
    }

    @Test
    void shouldMapEntitiesToPage() {
        CreditPage<MourabahaParametersModel> page =
            MourabahaParametersEntityMapper.mapToPage(List.of(entity()), 1L, 1, 20);

        assertEquals(1, page.getItems().size());
        assertEquals(1L, page.getItems().get(0).getId());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getLastPage());
        assertEquals(20, page.getSize());
        assertEquals(1, page.getTotal());
    }

    @Test
    void shouldMapToPageWithZeroSizeYieldsZeroLastPage() {
        CreditPage<MourabahaParametersModel> page =
            MourabahaParametersEntityMapper.mapToPage(List.of(entity()), 1L, 1, 0);

        assertEquals(0, page.getLastPage());
    }
}
