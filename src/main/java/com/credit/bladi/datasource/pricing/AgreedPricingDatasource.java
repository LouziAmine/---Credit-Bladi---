package com.credit.bladi.datasource.pricing;

import com.credit.bladi.datasource.pricing.entity.AgreedPricingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreedPricingDatasource extends JpaRepository<AgreedPricingEntity, Long> {
}
