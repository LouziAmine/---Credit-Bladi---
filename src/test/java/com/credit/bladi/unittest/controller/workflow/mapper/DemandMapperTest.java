package com.credit.bladi.unittest.controller.workflow.mapper;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_100000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_11250;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_120000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1200000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1300000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_150;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1800000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_200;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_21000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_23000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_252000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_300000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3300;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_33000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_340000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_36000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_36300;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3650;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_4600;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_4800;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_700000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_8000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_840000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_8500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_900000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_10_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_60;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_30;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_75;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLASSIFICATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLASSIFICATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FAKE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_KEEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MANUAL;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JEAN_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_JEAN_DUPONT;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSE_CONTRE_RISQUE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CHARGE_D_AFFAIRES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CLASSIFICATION_DU_DOSSIER;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INITIATION_DU_DOSSIER;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TRAITEMENT_ANALYSE_CONTRE_RISQUE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.workflow.request.AdvanceDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAmortizationResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandHistoryEntryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandResponseDTO;
import com.credit.bladi.controller.workflow.mapper.DemandMapper;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.model.workflow.StepClosureDetails;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DemandMapperTest {
    private static DemandModel demand(long id) {
        return MockDemand.enterprise(id, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null, null, null, null,
            null, null, null, null, null, null, null, null);
    }

    @Test
    void shouldAttachCurrentStepNamesWhenSummaryProvided() {
        DemandStepSummary summary = new DemandStepSummary(LABEL_ANALYSE_CONTRE_RISQUE,
            LABEL_TRAITEMENT_ANALYSE_CONTRE_RISQUE);

        DemandResponseDTO dto = DemandMapper.toResponseDTO(demand(1L), summary);

        assertEquals(LABEL_ANALYSE_CONTRE_RISQUE, dto.currentProcessName());
        assertEquals(LABEL_TRAITEMENT_ANALYSE_CONTRE_RISQUE, dto.currentStepName());
    }

    @Test
    void shouldLeaveCurrentStepNamesNullWhenSummaryMissing() {
        DemandResponseDTO dto = DemandMapper.toResponseDTO(demand(1L), null);

        assertNull(dto.currentProcessName());
        assertNull(dto.currentStepName());
    }

    @Test
    void shouldMergeMatchingSummaryPerDemandWhenMappingCreditPage() {
        CreditPage<DemandModel> source = new CreditPage<>();
        source.setItems(List.of(demand(1L), demand(2L)));
        source.setCurrentPage(1);
        source.setLastPage(1);
        source.setSize(20);
        source.setTotal(2);
        Map<Long, DemandStepSummary> currentSteps = Map.of(
            1L, new DemandStepSummary(LABEL_INITIATION_DU_DOSSIER, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES));

        CreditPage<DemandResponseDTO> result = DemandMapper.toCreditPage(source, currentSteps);

        assertEquals(LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, result.getItems().get(0).currentStepName());
        assertNull(result.getItems().get(1).currentStepName());
    }

    @Test
    void shouldMapAmountAndDurationOptionsWhenBuildingStepClosureDetails() {
        AdvanceDemandRequestDTO dto = new AdvanceDemandRequestDTO(
            null, null, null, null, null, null, null, null, null, null, CODE_KEEP, null,
            CODE_MANUAL, AMOUNT_2000000, CODE_MANUAL, 60);

        StepClosureDetails details = DemandMapper.toStepClosureDetails(dto);

        assertEquals(KeepOrManualOption.MANUAL, details.amountOption());
        assertEquals(AMOUNT_2000000, details.manualAmount());
        assertEquals(KeepOrManualOption.MANUAL, details.durationOption());
        assertEquals(60, details.manualDuration());
    }

    @Test
    void shouldMapAmountAndDurationFieldsWhenBuildingHistoryEntryResponse() {
        DemandHistoryEntryModel model = new DemandHistoryEntryModel(
            CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER, CODE_INITIATION_STEP,
            LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, null, null, null, null,
            USER_ADMIN, null, Instant.now(), Instant.now(), null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP.name(), DECIMAL_6_75, DECIMAL_6_75, USER_ADMIN,
            KeepOrManualOption.MANUAL.name(), AMOUNT_1500000, AMOUNT_2000000,
            KeepOrManualOption.KEEP.name(), 84, 84, null);

        DemandHistoryEntryResponseDTO dto = DemandMapper.toResponseDTO(model);

        assertEquals(CODE_MANUAL, dto.amountOption());
        assertEquals(AMOUNT_1500000, dto.previousAmount());
        assertEquals(AMOUNT_2000000, dto.appliedAmount());
        assertEquals(CODE_KEEP, dto.durationOption());
        assertEquals(84, dto.previousDuration());
        assertEquals(84, dto.appliedDuration());
    }

    @Test
    void shouldMapClassificationOutcomeWhenBuildingHistoryEntryResponse() {
        DemandHistoryEntryModel model = new DemandHistoryEntryModel(
            CODE_CLASSIFICATION, LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT, LABEL_CHARGE_D_AFFAIRES, null, null, null, null,
            USER_ADMIN, null, Instant.now(), Instant.now(), "Le client confirme sa demande", null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, CODE_FAKE);

        DemandHistoryEntryResponseDTO dto = DemandMapper.toResponseDTO(model);

        assertEquals(CODE_FAKE, dto.classificationOutcome());
    }

    @Test
    void shouldMapSectorCompanyAgeAndFinancialFieldsWhenBuildingDemandResponse() {
        DemandModel model = MockDemand.enterprise(
            1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME,
            Money.of(AMOUNT_1500000), 84, Instant.now(), null, null, null, null,
            Money.of(AMOUNT_8000000), Money.of(AMOUNT_100000),
            Money.of(AMOUNT_1200000), Money.of(AMOUNT_1300000), null, null,
            LABEL_TEXTILE, 5);

        DemandResponseDTO dto = DemandMapper.toResponseDTO(model);

        assertEquals(LABEL_TEXTILE, dto.enterprise().sector());
        assertEquals(5, dto.enterprise().companyAgeYears());
        assertEquals(AMOUNT_8000000, dto.enterprise().annualRevenue());
        assertEquals(AMOUNT_100000, dto.enterprise().existingAnnualDebtService());
        assertEquals(AMOUNT_1200000, dto.enterprise().netAnnualResult());
        assertEquals(AMOUNT_1300000, dto.enterprise().guaranteesValue());
    }

    @Test
    void shouldUseGivenAmountAndDurationRatherThanDemandsStoredValuesWhenBuildingAmortizationResponse() {
        DemandModel demandWithStaleValues = demand(1L);
        InvestmentFinancialSnapshot snapshot = new InvestmentFinancialSnapshot(Rate.of(AMOUNT_10),
            Money.of(AMOUNT_21000), Money.of(AMOUNT_252000),
            Money.of(AMOUNT_300000), Money.of(AMOUNT_1800000),
            Money.of(AMOUNT_11250), DECIMAL_10_50, List.of(),
            Money.of(AMOUNT_2000), Money.of(AMOUNT_23000), Money.of(AMOUNT_120000));

        DemandAmortizationResponseDTO dto = DemandMapper.toAmortizationResponseDTO(demandWithStaleValues,
            Money.of(AMOUNT_2000000), 60, snapshot);

        assertEquals(AMOUNT_2000000, dto.amount());
        assertEquals(60, dto.duration());
    }

    private static DemandModel mortgageDemand(UUID publicId) {
        return MockDemand.mortgage(1L, publicId, REFERENCE_DEM_ABCDEF12, 1L, PERSON_JEAN_DUPONT,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, Rate.of(DECIMAL_5_25),
            EMAIL_JEAN_AT_EXAMPLE_COM, PHONE_0612345678, null, ProductType.CONVENTIONAL, CspCode.SALA,
            Money.of(AMOUNT_600000), Money.of(AMOUNT_20000), 35, false, null);
    }

    @Test
    void shouldMapConventionalMortgageSnapshotWhenBuildingAmortizationResponse() {
        UUID publicId = UUID.randomUUID();
        DemandModel demand = mortgageDemand(publicId);
        FinancialSnapshot conventional = new FinancialSnapshot(
            Money.of(AMOUNT_3500), Money.of(AMOUNT_150), Money.of(AMOUNT_3650),
            Money.of(AMOUNT_340000), Money.of(AMOUNT_36000),
            Money.of(AMOUNT_840000), Money.of(AMOUNT_1000),
            Money.of(AMOUNT_500), Money.of(AMOUNT_2000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_8500));
        MortgageAmortizationSnapshot snapshot = new MortgageAmortizationSnapshot(ProductType.CONVENTIONAL,
            Rate.of(DECIMAL_5_25), DECIMAL_5_60, conventional, List.of(), null, List.of());

        DemandAmortizationResponseDTO dto = DemandMapper.toAmortizationResponseDTO(demand,
            Money.of(AMOUNT_500000), 240, snapshot);

        assertEquals(publicId, dto.demandId());
        assertEquals(0, DECIMAL_5_60.compareTo(dto.taeg()));
        assertEquals(0, AMOUNT_3500.compareTo(dto.conventional().monthlyPayment()));
        assertEquals(0, AMOUNT_340000.compareTo(dto.conventional().totalInterest()));
        assertEquals(0, AMOUNT_150.compareTo(dto.insurancePremium()));
        assertNull(dto.mourabaha());
    }

    @Test
    void shouldMapMourabahaSnapshotWhenBuildingAmortizationResponse() {
        UUID publicId = UUID.randomUUID();
        DemandModel demand = mortgageDemand(publicId);
        MourabahaFinancialSnapshot mourabaha = new MourabahaFinancialSnapshot(
            Money.of(AMOUNT_4600), Money.of(AMOUNT_33000),
            Money.of(AMOUNT_3300), Money.of(AMOUNT_36300),
            Money.of(AMOUNT_900000), Money.of(AMOUNT_1000),
            Money.of(AMOUNT_500), Money.of(AMOUNT_2000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_8500),
            Money.of(AMOUNT_200), Money.of(AMOUNT_4800),
            Money.of(AMOUNT_36000));
        MortgageAmortizationSnapshot snapshot = new MortgageAmortizationSnapshot(ProductType.MOURABAHA,
            Rate.of(DECIMAL_6_00), DECIMAL_6_30, null, List.of(), mourabaha, List.of());

        DemandAmortizationResponseDTO dto = DemandMapper.toAmortizationResponseDTO(demand,
            Money.of(AMOUNT_700000), 180, snapshot);

        assertNull(dto.conventional());
        assertEquals(0, AMOUNT_4600.compareTo(dto.mourabaha().monthlyPaymentTtc()));
        assertEquals(0, AMOUNT_33000.compareTo(dto.mourabaha().marginHt()));
        assertEquals(0, AMOUNT_200.compareTo(dto.insurancePremium()));
    }

    @Test
    void shouldDispatchToInvestmentOverloadWhenGivenDemandAmortizationResultWrappingInvestmentSnapshot() {
        DemandModel demand = demand(1L);
        InvestmentFinancialSnapshot snapshot = new InvestmentFinancialSnapshot(Rate.of(AMOUNT_10),
            Money.of(AMOUNT_21000), Money.of(AMOUNT_252000),
            Money.of(AMOUNT_300000), Money.of(AMOUNT_1800000),
            Money.of(AMOUNT_11250), DECIMAL_10_50, List.of(),
            Money.of(AMOUNT_2000), Money.of(AMOUNT_23000), Money.of(AMOUNT_120000));
        DemandAmortizationResult result = new DemandAmortizationResult(demand,
            Money.of(AMOUNT_2000000), 60, snapshot);

        DemandAmortizationResponseDTO dto = DemandMapper.toAmortizationResponseDTO(result);

        assertEquals(AMOUNT_2000000, dto.amount());
        assertEquals(60, dto.duration());
    }

    @Test
    void shouldDispatchToMortgageOverloadWhenGivenDemandAmortizationResultWrappingMortgageSnapshot() {
        UUID publicId = UUID.randomUUID();
        DemandModel demand = mortgageDemand(publicId);
        FinancialSnapshot conventional = new FinancialSnapshot(
            Money.of(AMOUNT_3500), Money.of(AMOUNT_150), Money.of(AMOUNT_3650),
            Money.of(AMOUNT_340000), Money.of(AMOUNT_36000),
            Money.of(AMOUNT_840000), Money.of(AMOUNT_1000),
            Money.of(AMOUNT_500), Money.of(AMOUNT_2000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_8500));
        MortgageAmortizationSnapshot snapshot = new MortgageAmortizationSnapshot(ProductType.CONVENTIONAL,
            Rate.of(DECIMAL_5_25), DECIMAL_5_60, conventional, List.of(), null, List.of());
        DemandAmortizationResult result = new DemandAmortizationResult(demand,
            Money.of(AMOUNT_500000), 240, snapshot);

        DemandAmortizationResponseDTO dto = DemandMapper.toAmortizationResponseDTO(result);

        assertEquals(publicId, dto.demandId());
        assertEquals(240, dto.duration());
    }
}
