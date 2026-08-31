package com.credit.bladi.controller.creditrate.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.vo.DurationRange;

public final class CreditRateMapper {
    private CreditRateMapper() {
    }

    public static CreditRateModel toDomain(Long id, PatchCreditRateDTO patchRequest) {
        return CreditRateModel.getBuilder()
            .id(id)
            .rateMin(toRate(patchRequest.rateMin()))
            .rateMax(toRate(patchRequest.rateMax()))
            .rateFormula(toRateFormula(patchRequest.rateFormula()))
            .durationRange(DurationRange.ofNullable(patchRequest.minDurationMonths(),
                patchRequest.maxDurationMonths()))
            .active(patchRequest.active())
            .build();
    }

    public static CreditRateResponseDTO toResponseDTO(CreditRateModel creditRate) {
        if (creditRate == null) {
            return null;
        }
        DurationRange durationRange = creditRate.getDurationRange();
        Integer minDurationMonths = durationRange != null ? durationRange.minMonths() : null;
        Integer maxDurationMonths = durationRange != null ? durationRange.maxMonths() : null;
        return new CreditRateResponseDTO(
            creditRate.getPublicId(),
            creditRate.getCspCode() != null ? creditRate.getCspCode().name() : null,
            toBigDecimal(creditRate.getRateMin()),
            toBigDecimal(creditRate.getRateMax()),
            creditRate.getRateFormula() != null ? creditRate.getRateFormula().name() : null,
            minDurationMonths,
            maxDurationMonths,
            creditRate.getActive());
    }

    public static CreditPage<CreditRateResponseDTO> toCreditPage(CreditPage<CreditRateModel> source) {
        return CreditPageMapper.map(source, CreditRateMapper::toResponseDTO);
    }

    private static RateFormula toRateFormula(String value) {
        return value != null ? RateFormula.valueOf(value) : null;
    }
}
