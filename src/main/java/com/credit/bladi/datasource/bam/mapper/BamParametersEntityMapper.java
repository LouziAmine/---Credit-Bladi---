package com.credit.bladi.datasource.bam.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import java.util.List;

public final class BamParametersEntityMapper {

    private BamParametersEntityMapper() {
    }

    public static CreditPage<BamParametersModel> mapToPage(
            List<BamParametersEntity> entities, long total, int page, int size) {
        CreditPage<BamParametersModel> result = new CreditPage<>();
        result.setItems(entities.stream().map(BamParametersEntityMapper::mapFromEntity).toList());
        result.setCurrentPage(page);
        result.setLastPage(size > 0 ? (int) Math.ceil((double) total / size) : 0);
        result.setSize(size);
        result.setTotal((int) total);
        return result;
    }

    public static BamParametersModel mapFromEntity(BamParametersEntity bamParametersEntity) {
        return BamParametersModel.getBuilder()
                .id(bamParametersEntity.getId())
                .processingFeesRatio(toRatio(bamParametersEntity.getProcessingFeesRatio()))
                .minProcessingFees(Money.of(bamParametersEntity.getMinProcessingFees()))
                .maxProcessingFees(Money.of(bamParametersEntity.getMaxProcessingFees()))
                .registrationFeeRatio(toRatio(bamParametersEntity.getRegistrationFeeRatio()))
                .landConservationRatio(toRatio(bamParametersEntity.getLandConservationRatio()))
                .notaryFeeRatio(toRatio(bamParametersEntity.getNotaryFeeRatio()))
                .ltvRatio(toRatio(bamParametersEntity.getLtvRatio()))
                .maxDebtRatio(toRatio(bamParametersEntity.getMaxDebtRatio()))
                .maxAgeAtMaturity(bamParametersEntity.getMaxAgeAtMaturity())
                .adiMonthlyRate(toRatio(bamParametersEntity.getAdiMonthlyRate()))
                .active(bamParametersEntity.getActive())
                .build();
    }

    public static BamParametersEntity mapToEntity(BamParametersModel bamParametersModel) {
        BamParametersEntity bamParametersEntity = new BamParametersEntity();
        bamParametersEntity.setId(bamParametersModel.getId());
        bamParametersEntity.setProcessingFeesRatio(toBigDecimal(bamParametersModel.getProcessingFeesRatio()));
        bamParametersEntity.setMinProcessingFees(toBigDecimal(bamParametersModel.getMinProcessingFees()));
        bamParametersEntity.setMaxProcessingFees(toBigDecimal(bamParametersModel.getMaxProcessingFees()));
        bamParametersEntity.setRegistrationFeeRatio(toBigDecimal(bamParametersModel.getRegistrationFeeRatio()));
        bamParametersEntity.setLandConservationRatio(toBigDecimal(bamParametersModel.getLandConservationRatio()));
        bamParametersEntity.setNotaryFeeRatio(toBigDecimal(bamParametersModel.getNotaryFeeRatio()));
        bamParametersEntity.setLtvRatio(toBigDecimal(bamParametersModel.getLtvRatio()));
        bamParametersEntity.setMaxDebtRatio(toBigDecimal(bamParametersModel.getMaxDebtRatio()));
        bamParametersEntity.setMaxAgeAtMaturity(bamParametersModel.getMaxAgeAtMaturity());
        bamParametersEntity.setAdiMonthlyRate(toBigDecimal(bamParametersModel.getAdiMonthlyRate()));
        bamParametersEntity.setActive(bamParametersModel.getActive());
        return bamParametersEntity;
    }

    private static Ratio toRatio(BigDecimal value) {
        return value != null ? Ratio.of(value) : null;
    }

    private static BigDecimal toBigDecimal(Ratio ratio) {
        return ratio != null ? ratio.value() : null;
    }

    private static BigDecimal toBigDecimal(Money money) {
        return money != null ? money.amount() : null;
    }
}