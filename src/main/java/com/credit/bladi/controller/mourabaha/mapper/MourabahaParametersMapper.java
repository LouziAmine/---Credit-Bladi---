package com.credit.bladi.controller.mourabaha.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRatio;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO;
import com.credit.bladi.controller.dto.mourabaha.response.MourabahaParametersResponseDTO;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;

public final class MourabahaParametersMapper {
    private MourabahaParametersMapper() {
    }

    public static MourabahaParametersModel toDomain(Long id, PatchMourabahaParametersDTO patchRequest) {
        return MourabahaParametersModel.getBuilder()
            .id(id)
            .profitRate(toRate(patchRequest.profitRate()))
            .vatRate(toRatio(patchRequest.vatRate()))
            .takafulMonthlyRate(toRate(patchRequest.takafulMonthlyRate()))
            .active(patchRequest.active())
            .build();
    }

    public static MourabahaParametersResponseDTO toResponseDTO(MourabahaParametersModel mourabahaParameters) {
        if (mourabahaParameters == null) {
            return null;
        }
        return new MourabahaParametersResponseDTO(
            mourabahaParameters.getPublicId(),
            toBigDecimal(mourabahaParameters.getProfitRate()),
            toBigDecimal(mourabahaParameters.getVatRate()),
            toBigDecimal(mourabahaParameters.getTakafulMonthlyRate()),
            mourabahaParameters.getActive());
    }

    public static CreditPage<MourabahaParametersResponseDTO> toCreditPage(
            CreditPage<MourabahaParametersModel> source) {
        return CreditPageMapper.map(source, MourabahaParametersMapper::toResponseDTO);
    }
}
