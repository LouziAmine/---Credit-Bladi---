package com.credit.bladi.controller.servicing.mapper;

import com.credit.bladi.controller.dto.servicing.response.CreditFileResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.InstallmentResponseDTO;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.workflow.DemandModel;
import java.math.BigDecimal;
import java.util.List;

public final class CreditFileMapper {
    private CreditFileMapper() {
    }

    public static CreditFileResponseDTO toResponseDTO(CreditFileModel creditFile, DemandModel demand,
            List<InstallmentModel> installments, PaymentBeneficiary beneficiary, String clientRib) {
        return new CreditFileResponseDTO(
            creditFile.publicId(),
            demand.publicId(),
            demand.reference(),
            demand.contactName(),
            creditFile.reference(),
            creditFile.disbursedAt(),
            creditFile.disbursedAmount().amount(),
            creditFile.status().name(),
            installments.stream().map(CreditFileMapper::toResponseDTO).toList(),
            beneficiary.name(),
            beneficiary.rib().value(),
            clientRib);
    }

    public static InstallmentResponseDTO toResponseDTO(InstallmentModel installment) {
        return new InstallmentResponseDTO(
            installment.publicId(),
            installment.sequenceNumber(),
            installment.dueDate(),
            installment.amount().amount(),
            installment.status().name(),
            installment.paidAt(),
            installment.declaredAt(),
            installment.declaredBy(),
            installment.declaredSourceRib(),
            installment.latePenalty() != null ? installment.latePenalty().amount() : BigDecimal.ZERO,
            installment.totalDue().amount());
    }
}
