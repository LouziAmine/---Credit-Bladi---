package com.credit.bladi.datasource.mourabaha.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRatio;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import java.util.List;

public final class MourabahaParametersEntityMapper {
    private MourabahaParametersEntityMapper() {
    }

    public static CreditPage<MourabahaParametersModel> mapToPage(
            List<MourabahaParametersEntity> entities, long total, int page, int size) {
        return CreditPage.of(entities.stream().map(MourabahaParametersEntityMapper::mapFromEntity).toList(),
            page, size, total);
    }

    public static MourabahaParametersModel mapFromEntity(MourabahaParametersEntity entity) {
        return MourabahaParametersModel.getBuilder()
                .id(entity.getId())
                .publicId(entity.getPublicId())
                .profitRate(toRate(entity.getProfitRate()))
                .vatRate(toRatio(entity.getVatRate()))
                .takafulMonthlyRate(toRate(entity.getTakafulMonthlyRate()))
                .active(entity.getActive())
                .build();
    }

    public static MourabahaParametersEntity mapToEntity(MourabahaParametersModel model) {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(model.getId());
        entity.setProfitRate(toBigDecimal(model.getProfitRate()));
        entity.setVatRate(toBigDecimal(model.getVatRate()));
        entity.setTakafulMonthlyRate(toBigDecimal(model.getTakafulMonthlyRate()));
        entity.setActive(model.getActive());
        return entity;
    }
}
