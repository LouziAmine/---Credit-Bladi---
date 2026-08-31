package com.credit.bladi.unittest.datasource.bam.mapper;

import static com.credit.bladi.unittest.mock.bam.MockBamParameters.BAM_ID;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.LAND_CONSERVATION_RATIO;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.NOTARY_FEE_RATIO;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.PROCESSING_FEES_RATIO;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.REGISTRATION_FEE_RATIO;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersEntity;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import com.credit.bladi.datasource.bam.mapper.BamParametersEntityMapper;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Ratio;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

class BamParametersEntityMapperTest {
    @Test
    void shouldMapEntityToModel() {
        BamParametersModel model = BamParametersEntityMapper.mapFromEntity(bamParametersEntity());

        assertTrue(EqualsBuilder.reflectionEquals(bamParametersModel(), model, "publicId"));
    }

    @Test
    void shouldMapModelToEntity() {
        BamParametersEntity entity = BamParametersEntityMapper.mapToEntity(bamParametersModel());

        assertTrue(EqualsBuilder.reflectionEquals(bamParametersEntity(), entity));
    }

    @Test
    void shouldMapPageWithCorrectLastPage() {
        List<BamParametersEntity> entities = List.of(bamParametersEntity(), bamParametersEntity());
        CreditPage<BamParametersModel> page = BamParametersEntityMapper.mapToPage(entities, 10L, 1, 3);

        assertEquals(2, page.getItems().size());
        assertEquals(10, page.getTotal());
        assertEquals(1, page.getCurrentPage());
        assertEquals(4, page.getLastPage());
        assertEquals(3, page.getSize());
    }

    @Test
    void shouldMapToPageWithZeroSizeYieldsZeroLastPage() {
        CreditPage<BamParametersModel> page =
            BamParametersEntityMapper.mapToPage(List.of(bamParametersEntity()), 10L, 1, 0);

        assertEquals(0, page.getLastPage());
    }

    @Test
    void shouldMapNullMoneyToNullBigDecimalInMapToEntity() {
        BamParametersModel modelWithNullMoney = BamParametersModel.getBuilder()
            .id(BAM_ID)
            .processingFeesRatio(Ratio.of(PROCESSING_FEES_RATIO))
            .minProcessingFees(null)
            .maxProcessingFees(null)
            .registrationFeeRatio(Ratio.of(REGISTRATION_FEE_RATIO))
            .landConservationRatio(Ratio.of(LAND_CONSERVATION_RATIO))
            .notaryFeeRatio(Ratio.of(NOTARY_FEE_RATIO))
            .active(true)
            .build();

        BamParametersEntity entity = BamParametersEntityMapper.mapToEntity(modelWithNullMoney);

        BamParametersEntity expected = new BamParametersEntity();
        expected.setId(BAM_ID);
        expected.setProcessingFeesRatio(PROCESSING_FEES_RATIO);
        expected.setRegistrationFeeRatio(REGISTRATION_FEE_RATIO);
        expected.setLandConservationRatio(LAND_CONSERVATION_RATIO);
        expected.setNotaryFeeRatio(NOTARY_FEE_RATIO);
        expected.setActive(true);

        assertTrue(EqualsBuilder.reflectionEquals(expected, entity));
    }
}
