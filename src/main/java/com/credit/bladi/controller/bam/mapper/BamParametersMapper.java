package com.credit.bladi.controller.bam.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toMoney;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRatio;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.model.bam.BamParametersModel;

public final class BamParametersMapper {
    private BamParametersMapper() {
    }

    public static BamParametersModel toDomain(Long id, PatchBamParametersDTO patchRequest) {
        return BamParametersModel.getBuilder()
            .id(id)
            .processingFeesRatio(toRatio(patchRequest.processingFeesRatio()))
            .minProcessingFees(toMoney(patchRequest.minProcessingFees()))
            .maxProcessingFees(toMoney(patchRequest.maxProcessingFees()))
            .registrationFeeRatio(toRatio(patchRequest.registrationFeeRatio()))
            .landConservationRatio(toRatio(patchRequest.landConservationRatio()))
            .notaryFeeRatio(toRatio(patchRequest.notaryFeeRatio()))
            .ltvRatio(toRatio(patchRequest.ltvRatio()))
            .maxDebtRatio(toRatio(patchRequest.maxDebtRatio()))
            .maxAgeAtMaturity(patchRequest.maxAgeAtMaturity())
            .adiMonthlyRate(toRatio(patchRequest.adiMonthlyRate()))
            .active(patchRequest.active())
            .build();
    }

    public static BamParametersResponseDTO toResponseDTO(BamParametersModel bamParameters) {
        if (bamParameters == null) {
            return null;
        }
        return new BamParametersResponseDTO(
            bamParameters.getPublicId(),
            toBigDecimal(bamParameters.getProcessingFeesRatio()),
            toBigDecimal(bamParameters.getMinProcessingFees()),
            toBigDecimal(bamParameters.getMaxProcessingFees()),
            toBigDecimal(bamParameters.getRegistrationFeeRatio()),
            toBigDecimal(bamParameters.getLandConservationRatio()),
            toBigDecimal(bamParameters.getNotaryFeeRatio()),
            toBigDecimal(bamParameters.getLtvRatio()),
            toBigDecimal(bamParameters.getMaxDebtRatio()),
            bamParameters.getMaxAgeAtMaturity(),
            toBigDecimal(bamParameters.getAdiMonthlyRate()),
            bamParameters.getActive());
    }

    public static CreditPage<BamParametersResponseDTO> toCreditPage(CreditPage<BamParametersModel> source) {
        return CreditPageMapper.map(source, BamParametersMapper::toResponseDTO);
    }
}
