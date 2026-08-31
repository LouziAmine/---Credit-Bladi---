package com.credit.bladi.datasource.bam.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRatio;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import java.util.List;

public final class BamParametersEntityMapper {
    private BamParametersEntityMapper() {
    }

    public static CreditPage<BamParametersModel> mapToPage(
            List<BamParametersEntity> entities, long total, int page, int size) {
        return CreditPage.of(entities.stream().map(BamParametersEntityMapper::mapFromEntity).toList(),
            page, size, total);
    }

    public static BamParametersModel mapFromEntity(BamParametersEntity bamParametersEntity) {
        return BamParametersModel.getBuilder()
                .id(bamParametersEntity.getId())
                .publicId(bamParametersEntity.getPublicId())
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
}
