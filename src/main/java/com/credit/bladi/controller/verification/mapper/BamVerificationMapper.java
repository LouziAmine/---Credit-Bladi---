package com.credit.bladi.controller.verification.mapper;

import com.credit.bladi.common.EnumParsers;
import com.credit.bladi.controller.dto.verification.response.BamVerificationResponseDTO;
import com.credit.bladi.controller.dto.verification.response.CentraleRisquesResultResponseDTO;
import com.credit.bladi.controller.dto.verification.response.ChequeIncidentResponseDTO;
import com.credit.bladi.controller.dto.verification.response.OutstandingLoanResponseDTO;
import com.credit.bladi.controller.dto.verification.response.ScipResultResponseDTO;
import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.model.verification.ChequeIncident;
import com.credit.bladi.model.verification.OutstandingLoan;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BamVerificationMapper {
    private static final String SOURCE = "MOCK_SIMULATION";

    private final MessageSource messageSource;

    public BamVerificationMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public BamVerificationResponseDTO toResponseDTO(BamVerificationResult result) {
        String disclaimer = messageSource.getMessage("bamVerification.disclaimer", null,
            LocaleContextHolder.getLocale());

        return new BamVerificationResponseDTO(
            SOURCE,
            disclaimer,
            result.verifiedIdentifier(),
            result.identifierType(),
            new ScipResultResponseDTO(result.scipStatus().name(), result.chequeIncidents().stream()
                .map(BamVerificationMapper::toResponseDTO)
                .toList()),
            new CentraleRisquesResultResponseDTO(result.centraleRisquesStatus().name(),
                result.outstandingLoans().stream()
                    .map(BamVerificationMapper::toResponseDTO)
                    .toList()),
            result.recommendation().name(),
            result.generatedAt()
        );
    }

    private static ChequeIncidentResponseDTO toResponseDTO(ChequeIncident incident) {
        return new ChequeIncidentResponseDTO(incident.type(), incident.date(), incident.amount(),
            incident.resolved());
    }

    private static OutstandingLoanResponseDTO toResponseDTO(OutstandingLoan outstandingLoan) {
        return new OutstandingLoanResponseDTO(outstandingLoan.institution(), outstandingLoan.creditType(),
            outstandingLoan.amount(), outstandingLoan.repaymentStatus().name());
    }

    public static EnterpriseType toEnterpriseType(String value) {
        return EnumParsers.parse(EnterpriseType.class, value, "enterpriseType");
    }

    public static BamScenario toScenario(String value) {
        return EnumParsers.parseNullable(BamScenario.class, value, "scenario");
    }
}
