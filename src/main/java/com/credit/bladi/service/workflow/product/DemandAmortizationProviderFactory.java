package com.credit.bladi.service.workflow.product;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DemandAmortizationProviderFactory extends DemandTypeRegistry<DemandAmortizationProvider> {
    public DemandAmortizationProviderFactory(List<DemandAmortizationProvider> providers) {
        super(providers, "amortization provider");
    }
}
