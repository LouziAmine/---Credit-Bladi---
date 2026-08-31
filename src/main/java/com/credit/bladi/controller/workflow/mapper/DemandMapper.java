package com.credit.bladi.controller.workflow.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.common.EnumParsers;
import com.credit.bladi.controller.dto.workflow.request.AdvanceDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.ConventionalAmortizationDetailResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAmortizationResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAssignmentHistoryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandCommentResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandHistoryEntryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandPipelineStepResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandStepResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.EnterpriseAmortizationDetailResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.EnterpriseDemandDetailResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.MortgageDemandDetailResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.MourabahaAmortizationDetailResponseDTO;
import com.credit.bladi.controller.investment.mapper.InvestmentCreditMapper;
import com.credit.bladi.controller.mortgage.mapper.MortgageCreditMapper;
import com.credit.bladi.model.enums.CommitteeType;
import com.credit.bladi.model.enums.DecisionOutcome;
import com.credit.bladi.model.enums.InsuranceCoverageType;
import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandAssignmentHistoryModel;
import com.credit.bladi.model.workflow.DemandCommentModel;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandPipelineStepModel;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.model.workflow.EnterpriseDemandDetail;
import com.credit.bladi.model.workflow.MortgageDemandDetail;
import com.credit.bladi.model.workflow.StepClosureDetails;
import java.util.Map;

public final class DemandMapper {
    private DemandMapper() {
    }

    public static StepClosureDetails toStepClosureDetails(AdvanceDemandRequestDTO dto) {
        DecisionOutcome decisionOutcome = EnumParsers.parseNullable(DecisionOutcome.class,
            dto.decisionOutcome(), "decisionOutcome");
        CommitteeType committeeType = EnumParsers.parseNullable(CommitteeType.class,
            dto.committeeType(), "committeeType");
        InsuranceCoverageType insuranceCoverageType = EnumParsers.parseNullable(InsuranceCoverageType.class,
            dto.insuranceCoverageType(), "insuranceCoverageType");
        KeepOrManualOption rateOption = EnumParsers.parse(KeepOrManualOption.class, dto.rateOption(), "rateOption");
        KeepOrManualOption amountOption = EnumParsers.parse(KeepOrManualOption.class, dto.amountOption(),
            "amountOption");
        KeepOrManualOption durationOption = EnumParsers.parse(KeepOrManualOption.class, dto.durationOption(),
            "durationOption");
        return new StepClosureDetails(dto.note(), decisionOutcome, dto.returnReasonCode(),
            dto.recommendations(), dto.reserves(), committeeType, dto.insuranceAdiSubscribed(),
            insuranceCoverageType, dto.insuranceDamageSubscribed(), rateOption, dto.manualRate(),
            amountOption, dto.manualAmount(), durationOption, dto.manualDuration());
    }

    public static DemandResponseDTO toResponseDTO(DemandModel model) {
        return toResponseDTO(model, null, null);
    }

    public static DemandResponseDTO toResponseDTO(DemandModel model, DemandStepSummary currentStep) {
        if (model == null || currentStep == null) {
            return toResponseDTO(model, null, null);
        }
        return toResponseDTO(model, currentStep.processName(), currentStep.stepName());
    }

    private static DemandResponseDTO toResponseDTO(DemandModel model, String currentProcessName,
            String currentStepName) {
        if (model == null) {
            return null;
        }
        EnterpriseDemandDetailResponseDTO enterprise = null;
        MortgageDemandDetailResponseDTO mortgage = null;
        switch (model.detail()) {
            case EnterpriseDemandDetail detail -> enterprise = new EnterpriseDemandDetailResponseDTO(
                detail.enterpriseType().name(),
                detail.enterprisePublicId(),
                detail.sector(),
                detail.companyAgeYears(),
                toBigDecimal(detail.annualRevenue()),
                toBigDecimal(detail.netAnnualResult()),
                toBigDecimal(detail.existingAnnualDebtService()),
                toBigDecimal(detail.guaranteesValue()));
            case MortgageDemandDetail detail -> mortgage = new MortgageDemandDetailResponseDTO(
                detail.productType().name(),
                detail.cspCode() != null ? detail.cspCode().name() : null,
                toBigDecimal(detail.propertyValue()),
                toBigDecimal(detail.monthlyIncome()));
        }
        return new DemandResponseDTO(
            model.publicId(),
            model.reference(),
            model.demandType().name(),
            model.contactName(),
            model.amount().amount(),
            model.duration(),
            model.createdAt(),
            model.completedAt(),
            model.isCompleted(),
            toBigDecimal(model.applicableRate()),
            model.contactEmail(),
            model.contactPhone(),
            currentProcessName,
            currentStepName,
            model.isRejected(),
            model.rejectedAt(),
            enterprise,
            mortgage);
    }

    public static DemandStepResponseDTO toResponseDTO(DemandStepModel model) {
        if (model == null) {
            return null;
        }
        return new DemandStepResponseDTO(
            model.processCode(),
            model.processName(),
            model.stepCode(),
            model.stepName(),
            model.typicalRole(),
            model.requiredGradeCode(),
            model.requiredGradeName(),
            model.requiredTeamCode(),
            model.requiredTeamName(),
            model.assignedUsername(),
            model.assignedBy(),
            model.enteredAt());
    }

    public static DemandHistoryEntryResponseDTO toResponseDTO(DemandHistoryEntryModel model) {
        return new DemandHistoryEntryResponseDTO(
            model.processCode(),
            model.processName(),
            model.stepCode(),
            model.stepName(),
            model.typicalRole(),
            model.requiredGradeCode(),
            model.requiredGradeName(),
            model.requiredTeamCode(),
            model.requiredTeamName(),
            model.assignedUsername(),
            model.assignedBy(),
            model.enteredAt(),
            model.exitedAt(),
            model.decisionNote(),
            model.decisionOutcome(),
            model.returnReasonCode(),
            model.recommendations(),
            model.reserves(),
            model.committeeType(),
            model.insuranceAdiSubscribed(),
            model.insuranceCoverageType(),
            model.insuranceDamageSubscribed(),
            model.rateOption(),
            model.previousRate(),
            model.appliedRate(),
            model.rateChangedBy(),
            model.amountOption(),
            model.previousAmount(),
            model.appliedAmount(),
            model.durationOption(),
            model.previousDuration(),
            model.appliedDuration(),
            model.classificationOutcome());
    }

    public static DemandPipelineStepResponseDTO toResponseDTO(DemandPipelineStepModel model) {
        return new DemandPipelineStepResponseDTO(
            model.processCode(),
            model.processName(),
            model.stepCode(),
            model.stepName(),
            model.typicalRole(),
            model.requiredGradeCode(),
            model.requiredGradeName(),
            model.requiredTeamCode(),
            model.requiredTeamName(),
            model.globalOrder(),
            model.status(),
            model.enteredAt(),
            model.exitedAt());
    }

    public static DemandCommentResponseDTO toResponseDTO(DemandCommentModel model) {
        return new DemandCommentResponseDTO(
            model.publicId(),
            model.stepCode(),
            model.stepName(),
            model.username(),
            model.comment(),
            model.createdAt());
    }

    public static DemandAssignmentHistoryResponseDTO toResponseDTO(DemandAssignmentHistoryModel model) {
        return new DemandAssignmentHistoryResponseDTO(
            model.publicId(),
            model.stepCode(),
            model.stepName(),
            model.previousUsername(),
            model.newUsername(),
            model.assignedBy(),
            model.assignedAt());
    }

    public static CreditPage<DemandResponseDTO> toCreditPage(CreditPage<DemandModel> source) {
        return CreditPageMapper.map(source, DemandMapper::toResponseDTO);
    }

    public static CreditPage<DemandResponseDTO> toCreditPage(CreditPage<DemandModel> source,
            Map<Long, DemandStepSummary> currentSteps) {
        return CreditPageMapper.map(source, demand -> toResponseDTO(demand, currentSteps.get(demand.id())));
    }

    public static DemandAmortizationResponseDTO toAmortizationResponseDTO(DemandModel demand, Money amount,
                                                                           Integer duration,
                                                                           InvestmentFinancialSnapshot snapshot) {
        return new DemandAmortizationResponseDTO(
            demand.publicId(),
            demand.reference(),
            amount.amount(),
            duration,
            snapshot.applicableRate().value(),
            snapshot.processingFees().amount(),
            snapshot.taeg(),
            snapshot.amortizationSchedule().stream().map(InvestmentCreditMapper::toRowDTO).toList(),
            snapshot.insurancePremium().amount(),
            snapshot.allInMonthlyPayment().amount(),
            snapshot.totalInsuranceCost().amount(),
            new EnterpriseAmortizationDetailResponseDTO(
                snapshot.monthlyPayment().amount(),
                snapshot.annualDebtService().amount(),
                snapshot.totalInterest().amount(),
                snapshot.totalCost().amount()),
            null,
            null);
    }

    public static DemandAmortizationResponseDTO toAmortizationResponseDTO(DemandModel demand, Money amount,
                                                                           Integer duration,
                                                                           MortgageAmortizationSnapshot snapshot) {
        if (snapshot.productType() == ProductType.MOURABAHA) {
            var mourabaha = snapshot.mourabaha();
            return new DemandAmortizationResponseDTO(
                demand.publicId(),
                demand.reference(),
                amount.amount(),
                duration,
                snapshot.applicableRate().value(),
                mourabaha.processingFees().amount(),
                snapshot.taeg(),
                snapshot.mourabahaAmortizationSchedule().stream().map(MortgageCreditMapper::toRowDTO).toList(),
                mourabaha.takafulMonthlyContribution().amount(),
                mourabaha.allInMonthlyPaymentTtc().amount(),
                mourabaha.totalTakafulCost().amount(),
                null,
                null,
                new MourabahaAmortizationDetailResponseDTO(
                    mourabaha.marginHt().amount(),
                    mourabaha.vatOnMargin().amount(),
                    mourabaha.marginTtc().amount(),
                    mourabaha.monthlyPaymentTtc().amount()));
        }
        var conventional = snapshot.conventional();
        return new DemandAmortizationResponseDTO(
            demand.publicId(),
            demand.reference(),
            amount.amount(),
            duration,
            snapshot.applicableRate().value(),
            conventional.processingFees().amount(),
            snapshot.taeg(),
            snapshot.amortizationSchedule().stream().map(MortgageCreditMapper::toRowDTO).toList(),
            conventional.adiMonthlyPremium().amount(),
            conventional.allInMonthlyPayment().amount(),
            conventional.totalAdiCost().amount(),
            null,
            new ConventionalAmortizationDetailResponseDTO(
                conventional.monthlyPayment().amount(),
                conventional.totalInterest().amount(),
                conventional.totalCost().amount()),
            null);
    }

    public static DemandAmortizationResponseDTO toAmortizationResponseDTO(DemandAmortizationResult result) {
        return switch (result.snapshot()) {
            case InvestmentFinancialSnapshot snapshot -> toAmortizationResponseDTO(result.demand(),
                result.resolvedAmount(), result.resolvedDuration(), snapshot);
            case MortgageAmortizationSnapshot snapshot -> toAmortizationResponseDTO(result.demand(),
                result.resolvedAmount(), result.resolvedDuration(), snapshot);
            default -> throw new IllegalStateException(
                "Unsupported amortization snapshot type: " + result.snapshot().getClass());
        };
    }
}
