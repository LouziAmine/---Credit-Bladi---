package com.credit.bladi.datasource.creditrate.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.util.List;

public final class CreditRateEntityMapper {
    private CreditRateEntityMapper() {
    }

    public static CreditPage<CreditRateModel> mapToPage(
            List<CreditRateEntity> entities, long total, int page, int size) {
        return CreditPage.of(entities.stream().map(CreditRateEntityMapper::mapFromEntity).toList(), page, size, total);
    }

    public static CreditRateModel mapFromEntity(CreditRateEntity creditRateEntity) {
        return CreditRateModel.getBuilder()
                .id(creditRateEntity.getId())
                .publicId(creditRateEntity.getPublicId())
                .cspCode(CspCode.valueOf(creditRateEntity.getCspCode()))
                .rateMin(Rate.of(creditRateEntity.getRateMin()))
                .rateMax(Rate.of(creditRateEntity.getRateMax()))
                .rateFormula(
                    creditRateEntity.getRateFormula() != null
                        ? RateFormula.valueOf(creditRateEntity.getRateFormula()) : null)
                .durationRange(
                    DurationRange.of(creditRateEntity.getMinDurationMonths(), creditRateEntity.getMaxDurationMonths()))
                .active(creditRateEntity.getActive())
                .build();
    }

    public static CreditRateEntity mapToEntity(CreditRateModel creditRateModel) {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(creditRateModel.getId());
        entity.setRateMin(toBigDecimal(creditRateModel.getRateMin()));
        entity.setRateMax(toBigDecimal(creditRateModel.getRateMax()));
        entity.setRateFormula(
            creditRateModel.getRateFormula() != null ? creditRateModel.getRateFormula().name() : null);
        DurationRange durationRange = creditRateModel.getDurationRange();
        if (durationRange != null) {
            entity.setMinDurationMonths(durationRange.minMonths());
            entity.setMaxDurationMonths(durationRange.maxMonths());
        }
        entity.setActive(creditRateModel.getActive());
        return entity;
    }
}
