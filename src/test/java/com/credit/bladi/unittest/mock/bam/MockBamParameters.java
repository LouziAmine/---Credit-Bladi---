package com.credit.bladi.unittest.mock.bam;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import java.util.List;

public final class MockBamParameters {

    public static final Long BAM_ID = 1L;
    public static final BigDecimal PROCESSING_FEES_RATIO = new BigDecimal("0.003");
    public static final BigDecimal MIN_FEES = new BigDecimal("3000");
    public static final BigDecimal MAX_FEES = new BigDecimal("6000");
    public static final BigDecimal REGISTRATION_FEE_RATIO = new BigDecimal("0.04");
    public static final BigDecimal LAND_CONSERVATION_RATIO = new BigDecimal("0.015");
    public static final BigDecimal NOTARY_FEE_RATIO = new BigDecimal("0.01");
    public static final BigDecimal LTV_RATIO = new BigDecimal("0.9000");
    public static final BigDecimal MAX_DEBT_RATIO = new BigDecimal("0.4000");
    public static final Integer MAX_AGE_AT_MATURITY = 75;
    public static final BigDecimal ADI_MONTHLY_RATE = new BigDecimal("0.0004");
    public static final BigDecimal PATCH_PROCESSING_FEES_RATIO = new BigDecimal("0.005");
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
        BamParametersResponseDTO dto = new BamParametersResponseDTO();
        dto.setId(BAM_ID);
        dto.setProcessingFeesRatio(PROCESSING_FEES_RATIO);
        dto.setMinProcessingFees(MIN_FEES);
        dto.setMaxProcessingFees(MAX_FEES);
        dto.setRegistrationFeeRatio(REGISTRATION_FEE_RATIO);
        dto.setLandConservationRatio(LAND_CONSERVATION_RATIO);
        dto.setNotaryFeeRatio(NOTARY_FEE_RATIO);
        dto.setLtvRatio(LTV_RATIO);
        dto.setMaxDebtRatio(MAX_DEBT_RATIO);
        dto.setMaxAgeAtMaturity(MAX_AGE_AT_MATURITY);
        dto.setAdiMonthlyRate(ADI_MONTHLY_RATE);
        dto.setActive(true);
        return dto;
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
        PatchBamParametersDTO dto = new PatchBamParametersDTO();
        dto.setProcessingFeesRatio(PATCH_PROCESSING_FEES_RATIO);
        return dto;
    }
}