package com.credit.bladi.datasource.mourabaha.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import java.util.List;

public final class MourabahaParametersEntityMapper {

    private MourabahaParametersEntityMapper() {
    }

    public static CreditPage<MourabahaParametersModel> mapToPage(
            List<MourabahaParametersEntity> entities, long total, int page, int size) {
        CreditPage<MourabahaParametersModel> result = new CreditPage<>();
        result.setItems(entities.stream().map(MourabahaParametersEntityMapper::mapFromEntity).toList());
        result.setCurrentPage(page);
        result.setLastPage(size > 0 ? (int) Math.ceil((double) total / size) : 0);
        result.setSize(size);
        result.setTotal((int) total);
        return result;
    }

    public static MourabahaParametersModel mapFromEntity(MourabahaParametersEntity entity) {
        return MourabahaParametersModel.getBuilder()
                .id(entity.getId())
                .profitRate(toRate(entity.getProfitRate()))
                .vatRate(toRatio(entity.getVatRate()))
                .active(entity.getActive())
                .build();
    }

    public static MourabahaParametersEntity mapToEntity(MourabahaParametersModel model) {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(model.getId());
        entity.setProfitRate(toBigDecimal(model.getProfitRate()));
        entity.setVatRate(toBigDecimal(model.getVatRate()));
        entity.setActive(model.getActive());
        return entity;
    }

    private static Rate toRate(BigDecimal value) {
        return value != null ? Rate.of(value) : null;
    }

    private static Ratio toRatio(BigDecimal value) {
        return value != null ? Ratio.of(value) : null;
    }

    private static BigDecimal toBigDecimal(Rate rate) {
        return rate != null ? rate.value() : null;
    }

    private static BigDecimal toBigDecimal(Ratio ratio) {
        return ratio != null ? ratio.value() : null;
    }
}
