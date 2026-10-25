package com.credit.bladi.unittest.service.mortgage;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_700000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_60;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_30;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JEAN_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_JEAN_DUPONT;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.mortgage.IMortgageCreditService;
import com.credit.bladi.service.mortgage.MortgageAmortizationProvider;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageAmortizationProviderTest {
    @Mock
    private IMortgageCreditService mortgageCreditService;

    private MortgageAmortizationProvider provider;

    private static final UUID DEMAND_PUBLIC_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        provider = new MortgageAmortizationProvider(mortgageCreditService);
    }

    private static DemandModel mortgageDemand() {
        return MockDemand.mortgage(1L, DEMAND_PUBLIC_ID, REFERENCE_DEM_ABCDEF12, 1L, PERSON_JEAN_DUPONT,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, Rate.of(DECIMAL_5_25),
            EMAIL_JEAN_AT_EXAMPLE_COM, PHONE_0612345678, null, ProductType.CONVENTIONAL, CspCode.SALA,
            Money.of(AMOUNT_600000), Money.of(AMOUNT_20000), 35, false, null);
    }

    @Test
    void shouldSupportMortgageDemandType() {
        assertEquals(DemandType.MORTGAGE, provider.supportedType());
    }

    @Test
    void shouldUseDemandAmountAndDurationAndPassSnapshotThroughUnchangedWhenNoOverrideGiven() {
        DemandModel demand = mortgageDemand();
        MortgageAmortizationSnapshot snapshot = new MortgageAmortizationSnapshot(ProductType.CONVENTIONAL,
            Rate.of(DECIMAL_5_25), DECIMAL_5_60, null, List.of(), null, List.of());
        when(mortgageCreditService.getAmortizationSchedule(1L, null, null, null)).thenReturn(snapshot);

        DemandAmortizationResult result = provider.getAmortizationSchedule(demand, null, null, null);

        assertEquals(demand, result.demand());
        assertEquals(0, AMOUNT_500000.compareTo(result.resolvedAmount().amount()));
        assertEquals(240, result.resolvedDuration());
        assertSame(snapshot, result.snapshot());
        verify(mortgageCreditService).getAmortizationSchedule(1L, null, null, null);
    }

    @Test
    void shouldApplyAmountAndDurationOverridesAndDelegateOverridesToMortgageCreditService() {
        DemandModel demand = mortgageDemand();
        Money amountOverride = Money.of(AMOUNT_700000);
        Rate rateOverride = Rate.of(DECIMAL_6_00);
        Integer durationOverride = 180;
        MortgageAmortizationSnapshot snapshot = new MortgageAmortizationSnapshot(ProductType.MOURABAHA,
            rateOverride, DECIMAL_6_30, null, List.of(), null, List.of());
        when(mortgageCreditService.getAmortizationSchedule(1L, amountOverride, rateOverride, durationOverride))
            .thenReturn(snapshot);

        DemandAmortizationResult result = provider.getAmortizationSchedule(demand, amountOverride, rateOverride,
            durationOverride);

        assertEquals(0, AMOUNT_700000.compareTo(result.resolvedAmount().amount()));
        assertEquals(180, result.resolvedDuration());
        assertSame(snapshot, result.snapshot());
        verify(mortgageCreditService).getAmortizationSchedule(1L, amountOverride, rateOverride, durationOverride);
    }
}
