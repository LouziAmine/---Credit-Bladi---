package com.credit.bladi.controller.bam.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;

public final class BamParametersMapper {

    private BamParametersMapper() {
    }

    public static BamParametersModel toDomain(Long id, PatchBamParametersDTO patchRequest) {
        return BamParametersModel.getBuilder()
            .id(id)
            .processingFeesRatio(toRatio(patchRequest.getProcessingFeesRatio()))
            .minProcessingFees(toMoney(patchRequest.getMinProcessingFees()))
            .maxProcessingFees(toMoney(patchRequest.getMaxProcessingFees()))
            .registrationFeeRatio(toRatio(patchRequest.getRegistrationFeeRatio()))
            .landConservationRatio(toRatio(patchRequest.getLandConservationRatio()))
            .notaryFeeRatio(toRatio(patchRequest.getNotaryFeeRatio()))
            .ltvRatio(toRatio(patchRequest.getLtvRatio()))
            .maxDebtRatio(toRatio(patchRequest.getMaxDebtRatio()))
            .maxAgeAtMaturity(patchRequest.getMaxAgeAtMaturity())
            .adiMonthlyRate(toRatio(patchRequest.getAdiMonthlyRate()))
            .active(patchRequest.getActive())
            .build();
    }

    public static BamParametersResponseDTO toResponseDTO(BamParametersModel bamParameters) {
        if (bamParameters == null) {
            return null;
        }
        BamParametersResponseDTO bamParametersResponseDTO = new BamParametersResponseDTO();
        bamParametersResponseDTO.setId(bamParameters.getId());
        bamParametersResponseDTO.setProcessingFeesRatio(toBigDecimal(bamParameters.getProcessingFeesRatio()));
        bamParametersResponseDTO.setMinProcessingFees(toBigDecimal(bamParameters.getMinProcessingFees()));
        bamParametersResponseDTO.setMaxProcessingFees(toBigDecimal(bamParameters.getMaxProcessingFees()));
        bamParametersResponseDTO.setRegistrationFeeRatio(toBigDecimal(bamParameters.getRegistrationFeeRatio()));
        bamParametersResponseDTO.setLandConservationRatio(toBigDecimal(bamParameters.getLandConservationRatio()));
        bamParametersResponseDTO.setNotaryFeeRatio(toBigDecimal(bamParameters.getNotaryFeeRatio()));
        bamParametersResponseDTO.setLtvRatio(toBigDecimal(bamParameters.getLtvRatio()));
        bamParametersResponseDTO.setMaxDebtRatio(toBigDecimal(bamParameters.getMaxDebtRatio()));
        bamParametersResponseDTO.setMaxAgeAtMaturity(bamParameters.getMaxAgeAtMaturity());
        bamParametersResponseDTO.setAdiMonthlyRate(toBigDecimal(bamParameters.getAdiMonthlyRate()));
        bamParametersResponseDTO.setActive(bamParameters.getActive());
        return bamParametersResponseDTO;
    }

    private static Ratio toRatio(BigDecimal value) {
        return value != null ? Ratio.of(value) : null;
    }

    private static Money toMoney(BigDecimal value) {
        return value != null ? Money.of(value) : null;
    }

    private static BigDecimal toBigDecimal(Ratio ratio) {
        return ratio != null ? ratio.value() : null;
    }

    private static BigDecimal toBigDecimal(Money money) {
        return money != null ? money.amount() : null;
    }

    public static CreditPage<BamParametersResponseDTO> toCreditPage(CreditPage<BamParametersModel> source) {
        return CreditPageMapper.map(source, BamParametersMapper::toResponseDTO);
    }
}