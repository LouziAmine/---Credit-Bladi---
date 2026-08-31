package com.credit.bladi.unittest.service.workflow;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ENTERPRISE;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.service.workflow.product.DemandAmortizationProvider;
import com.credit.bladi.service.workflow.product.DemandAmortizationProviderFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DemandAmortizationProviderFactoryTest {
    @Mock
    private DemandAmortizationProvider mortgageProvider;

    @Mock
    private DemandAmortizationProvider enterpriseProvider;

    @Test
    void shouldResolveTheProviderRegisteredForEachDemandType() {
        when(mortgageProvider.supportedType()).thenReturn(DemandType.MORTGAGE);
        when(enterpriseProvider.supportedType()).thenReturn(DemandType.ENTERPRISE);
        DemandAmortizationProviderFactory factory =
            new DemandAmortizationProviderFactory(List.of(mortgageProvider, enterpriseProvider));

        assertSame(mortgageProvider, factory.get(DemandType.MORTGAGE));
        assertSame(enterpriseProvider, factory.get(DemandType.ENTERPRISE));
    }

    @Test
    void shouldFailFastAtStartupWhenADemandTypeHasNoProvider() {
        when(mortgageProvider.supportedType()).thenReturn(DemandType.MORTGAGE);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> new DemandAmortizationProviderFactory(List.of(mortgageProvider)));

        assertTrue(exception.getMessage().contains(CODE_ENTERPRISE));
    }

    @Test
    void shouldFailFastAtStartupWhenTwoProvidersClaimTheSameDemandType() {
        when(mortgageProvider.supportedType()).thenReturn(DemandType.MORTGAGE);
        when(enterpriseProvider.supportedType()).thenReturn(DemandType.MORTGAGE);

        assertThrows(IllegalStateException.class,
            () -> new DemandAmortizationProviderFactory(List.of(mortgageProvider, enterpriseProvider)));
    }
}
