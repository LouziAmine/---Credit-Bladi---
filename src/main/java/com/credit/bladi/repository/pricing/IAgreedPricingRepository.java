package com.credit.bladi.repository.pricing;

import com.credit.bladi.model.pricing.AgreedPricing;
import java.util.Optional;

public interface IAgreedPricingRepository {
    void save(AgreedPricing pricing);

    Optional<AgreedPricing> findByDemandId(Long demandId);
}
