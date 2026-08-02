package com.credit.bladi.controller.creditrate.mapper;

import static com.credit.bladi.exception.CreditRateException.CreditRateExceptionType.INVALID_DURATION_RANGE;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;

public final class CreditRateMapper {

    private CreditRateMapper() {
    }

    public static CreditRateModel toDomain(Long id, PatchCreditRateDTO patchRequest) {
        return CreditRateModel.getBuilder()
            .id(id)
            .rateMin(toRate(patchRequest.getRateMin()))
            .rateMax(toRate(patchRequest.getRateMax()))
            .durationRange(toDurationRange(patchRequest.getMinDurationMonths(), patchRequest.getMaxDurationMonths()))
            .active(patchRequest.getActive())
            .build();
    }

    public static CreditRateResponseDTO toResponseDTO(CreditRateModel creditRate) {
        if (creditRate == null) {
            return null;
        }
        CreditRateResponseDTO dto = new CreditRateResponseDTO();
        dto.setId(creditRate.getId());
        dto.setCspCode(creditRate.getCspCode() != null ? creditRate.getCspCode().name() : null);
        dto.setRateMin(toBigDecimal(creditRate.getRateMin()));
        dto.setRateMax(toBigDecimal(creditRate.getRateMax()));
        DurationRange durationRange = creditRate.getDurationRange();
        if (durationRange != null) {
            dto.setMinDurationMonths(durationRange.minMonths());
            dto.setMaxDurationMonths(durationRange.maxMonths());
        }
        dto.setActive(creditRate.getActive());
        return dto;
    }

    public static CreditPage<CreditRateResponseDTO> toCreditPage(CreditPage<CreditRateModel> source) {
        return CreditPageMapper.map(source, CreditRateMapper::toResponseDTO);
    }

    private static Rate toRate(BigDecimal value) {
        return value != null ? Rate.of(value) : null;
    }

    private static BigDecimal toBigDecimal(Rate rate) {
        return rate != null ? rate.value() : null;
    }

    private static DurationRange toDurationRange(Integer minDurationMonths, Integer maxDurationMonths) {
        if (minDurationMonths == null && maxDurationMonths == null) {
            return null;
        }
        if (minDurationMonths == null || maxDurationMonths == null) {
            throw CreditRateException.of(INVALID_DURATION_RANGE);
        }
        return DurationRange.of(minDurationMonths, maxDurationMonths);
    }
}
