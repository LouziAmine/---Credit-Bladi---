package com.credit.bladi.unittest.datasource.creditrate.mapper;

import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.creditRateEntity;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.creditRateModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.datasource.creditrate.mapper.CreditRateEntityMapper;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.vo.DurationRange;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

class CreditRateEntityMapperTest {
    @Test
    void shouldMapEntityToModel() {
        CreditRateModel model = CreditRateEntityMapper.mapFromEntity(creditRateEntity());

        assertTrue(EqualsBuilder.reflectionEquals(creditRateModel(), model));
    }

    @Test
    void shouldMapModelToEntityWithDurationRange() {
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L)
            .durationRange(DurationRange.of(12, 300))
            .build();

        CreditRateEntity entity = CreditRateEntityMapper.mapToEntity(model);

        assertEquals(12, entity.getMinDurationMonths());
        assertEquals(300, entity.getMaxDurationMonths());
    }

    @Test
    void shouldMapModelWithoutDurationRangeToEntity() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).build();

        CreditRateEntity entity = CreditRateEntityMapper.mapToEntity(model);

        assertNull(entity.getMinDurationMonths());
        assertNull(entity.getMaxDurationMonths());
    }

    @Test
    void shouldMapEntitiesToPage() {
        CreditPage<CreditRateModel> page =
            CreditRateEntityMapper.mapToPage(List.of(creditRateEntity()), 1L, 1, 20);

        assertEquals(1, page.getItems().size());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getLastPage());
        assertEquals(20, page.getSize());
        assertEquals(1, page.getTotal());
    }

    @Test
    void shouldMapToPageWithZeroSizeYieldsZeroLastPage() {
        CreditPage<CreditRateModel> page =
            CreditRateEntityMapper.mapToPage(List.of(creditRateEntity()), 1L, 1, 0);

        assertEquals(0, page.getLastPage());
    }
}
