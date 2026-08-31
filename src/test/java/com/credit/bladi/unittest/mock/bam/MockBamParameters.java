package com.credit.bladi.unittest.mock.bam;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_6000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_003;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_005;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_04;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_4000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_9000;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class MockBamParameters {
    public static final Long BAM_ID = 1L;
    public static final UUID BAM_PUBLIC_ID = UUID.randomUUID();
    public static final BigDecimal PROCESSING_FEES_RATIO = DECIMAL_0_003;
    public static final BigDecimal MIN_FEES = AMOUNT_3000;
    public static final BigDecimal MAX_FEES = AMOUNT_6000;
    public static final BigDecimal REGISTRATION_FEE_RATIO = DECIMAL_0_04;
    public static final BigDecimal LAND_CONSERVATION_RATIO = DECIMAL_0_015;
    public static final BigDecimal NOTARY_FEE_RATIO = DECIMAL_0_01;
    public static final BigDecimal LTV_RATIO = DECIMAL_0_9000;
    public static final BigDecimal MAX_DEBT_RATIO = DECIMAL_0_4000;
    public static final Integer MAX_AGE_AT_MATURITY = 75;
    public static final BigDecimal ADI_MONTHLY_RATE = DECIMAL_0_0004;
    public static final BigDecimal PATCH_PROCESSING_FEES_RATIO = DECIMAL_0_005;
    public static final Integer PAGE = 1;
    public static final Integer SIZE = 20;
    public static final Integer TOTAL = 1;

    private MockBamParameters() {}

    public static BamParametersEntity bamParametersEntity() {
        BamParametersEntity entity = new BamParametersEntity();
        entity.setId(BAM_ID);
        entity.setProcessingFeesRatio(PROCESSING_FEES_RATIO);
        entity.setMinProcessingFees(MIN_FEES);
        entity.setMaxProcessingFees(MAX_FEES);
        entity.setRegistrationFeeRatio(REGISTRATION_FEE_RATIO);
        entity.setLandConservationRatio(LAND_CONSERVATION_RATIO);
        entity.setNotaryFeeRatio(NOTARY_FEE_RATIO);
        entity.setLtvRatio(LTV_RATIO);
        entity.setMaxDebtRatio(MAX_DEBT_RATIO);
        entity.setMaxAgeAtMaturity(MAX_AGE_AT_MATURITY);
        entity.setAdiMonthlyRate(ADI_MONTHLY_RATE);
        entity.setActive(true);
        return entity;
    }

    public static BamParametersModel bamParametersModel() {
        return BamParametersModel.getBuilder()
            .id(BAM_ID)
            .publicId(BAM_PUBLIC_ID)
            .processingFeesRatio(Ratio.of(PROCESSING_FEES_RATIO))
            .minProcessingFees(Money.of(MIN_FEES))
            .maxProcessingFees(Money.of(MAX_FEES))
            .registrationFeeRatio(Ratio.of(REGISTRATION_FEE_RATIO))
            .landConservationRatio(Ratio.of(LAND_CONSERVATION_RATIO))
            .notaryFeeRatio(Ratio.of(NOTARY_FEE_RATIO))
            .ltvRatio(Ratio.of(LTV_RATIO))
            .maxDebtRatio(Ratio.of(MAX_DEBT_RATIO))
            .maxAgeAtMaturity(MAX_AGE_AT_MATURITY)
            .adiMonthlyRate(Ratio.of(ADI_MONTHLY_RATE))
            .active(true)
            .build();
    }

    public static BamParametersResponseDTO bamParametersResponseDTO() {
        return new BamParametersResponseDTO(
            BAM_PUBLIC_ID,
            PROCESSING_FEES_RATIO,
            MIN_FEES,
            MAX_FEES,
            REGISTRATION_FEE_RATIO,
            LAND_CONSERVATION_RATIO,
            NOTARY_FEE_RATIO,
            LTV_RATIO,
            MAX_DEBT_RATIO,
            MAX_AGE_AT_MATURITY,
            ADI_MONTHLY_RATE,
            true);
    }

    public static CreditPage<BamParametersModel> bamParametersPage() {
        CreditPage<BamParametersModel> page = new CreditPage<>();
        page.setItems(List.of(bamParametersModel()));
        page.setTotal(TOTAL);
        page.setCurrentPage(PAGE);
        page.setLastPage(PAGE);
        page.setSize(SIZE);
        return page;
    }

    public static PatchBamParametersDTO patchRequest() {
        return new PatchBamParametersDTO(
            PATCH_PROCESSING_FEES_RATIO, null, null, null, null, null, null, null, null, null, null);
    }
}
