package com.credit.bladi.datasource.creditrate.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;

public final class CreditRateEntityMapper {

    private CreditRateEntityMapper() {
    }

    public static CreditPage<CreditRateModel> mapToPage(
            List<CreditRateEntity> entities, long total, int page, int size) {
        CreditPage<CreditRateModel> result = new CreditPage<>();
        result.setItems(entities.stream().map(CreditRateEntityMapper::mapFromEntity).toList());
        result.setCurrentPage(page);
        result.setLastPage(size > 0 ? (int) Math.ceil((double) total / size) : 0);
        result.setSize(size);
        result.setTotal((int) total);
        return result;
    }

    public static CreditRateModel mapFromEntity(CreditRateEntity creditRateEntity) {
        return CreditRateModel.getBuilder()
                .id(creditRateEntity.getId())
                .cspCode(CspCode.valueOf(creditRateEntity.getCspCode()))
                .rateMin(Rate.of(creditRateEntity.getRateMin()))
                .rateMax(Rate.of(creditRateEntity.getRateMax()))
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
        DurationRange durationRange = creditRateModel.getDurationRange();
        if (durationRange != null) {
            entity.setMinDurationMonths(durationRange.minMonths());
            entity.setMaxDurationMonths(durationRange.maxMonths());
        }
        entity.setActive(creditRateModel.getActive());
        return entity;
    }

    private static BigDecimal toBigDecimal(Rate rate) {
        return rate != null ? rate.value() : null;
    }
}