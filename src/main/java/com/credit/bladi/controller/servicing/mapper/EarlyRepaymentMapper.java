package com.credit.bladi.controller.servicing.mapper;

import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentQuoteResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentQuoteResponseDTO.RescheduledInstallmentResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentResponseDTO;
import com.credit.bladi.model.servicing.EarlyRepaymentModel;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote;
import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;

public final class EarlyRepaymentMapper {
    private EarlyRepaymentMapper() {
    }

    public static Money toPrincipal(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    public static EarlyRepaymentQuoteResponseDTO toResponseDTO(EarlyRepaymentQuote quote) {
        return new EarlyRepaymentQuoteResponseDTO(
            quote.fullRepayment(),
            quote.valueDate(),
            quote.outstandingBefore().amount(),
            quote.repaidPrincipal().amount(),
            quote.accruedInterest().amount(),
            quote.indemnity().amount(),
            quote.totalToPay().amount(),
            quote.outstandingAfter().amount(),
            quote.newSchedule().isEmpty() ? null : quote.newSchedule().getFirst().amount().amount(),
            quote.newSchedule().stream()
                .map(row -> new RescheduledInstallmentResponseDTO(row.sequenceNumber(), row.dueDate(),
                    row.amount().amount()))
                .toList());
    }

    public static EarlyRepaymentResponseDTO toResponseDTO(EarlyRepaymentModel model) {
        return new EarlyRepaymentResponseDTO(
            model.publicId(),
            model.fullRepayment(),
            model.outstandingBefore().amount(),
            model.repaidPrincipal().amount(),
            model.accruedInterest().amount(),
            model.indemnity().amount(),
            model.totalPaid().amount(),
            model.outstandingAfter().amount(),
            model.performedBy(),
            model.performedAt());
    }
}
