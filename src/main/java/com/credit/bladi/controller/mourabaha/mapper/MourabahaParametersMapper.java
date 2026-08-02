package com.credit.bladi.controller.mourabaha.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO;
import com.credit.bladi.controller.dto.mourabaha.response.MourabahaParametersResponseDTO;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;

public final class MourabahaParametersMapper {

    private MourabahaParametersMapper() {
    }

    public static MourabahaParametersModel toDomain(Long id, PatchMourabahaParametersDTO patchRequest) {
        return MourabahaParametersModel.getBuilder()
            .id(id)
            .profitRate(toRate(patchRequest.getProfitRate()))
            .vatRate(toRatio(patchRequest.getVatRate()))
            .active(patchRequest.getActive())
            .build();
    }

    public static MourabahaParametersResponseDTO toResponseDTO(MourabahaParametersModel mourabahaParameters) {
        if (mourabahaParameters == null) {
            return null;
        }
        MourabahaParametersResponseDTO dto = new MourabahaParametersResponseDTO();
        dto.setId(mourabahaParameters.getId());
        dto.setProfitRate(toBigDecimal(mourabahaParameters.getProfitRate()));
        dto.setVatRate(toBigDecimal(mourabahaParameters.getVatRate()));
        dto.setActive(mourabahaParameters.getActive());
        return dto;
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

    public static CreditPage<MourabahaParametersResponseDTO> toCreditPage(
            CreditPage<MourabahaParametersModel> source) {
        return CreditPageMapper.map(source, MourabahaParametersMapper::toResponseDTO);
    }
}
