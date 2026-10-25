package com.credit.bladi.repository.pricing;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toMoney;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRatio;

import com.credit.bladi.datasource.pricing.AgreedPricingDatasource;
import com.credit.bladi.datasource.pricing.entity.AgreedPricingEntity;
import com.credit.bladi.model.pricing.AgreedPricing;
import com.credit.bladi.model.pricing.AgreedPricing.InvestmentFees;
import com.credit.bladi.model.pricing.AgreedPricing.MortgageFees;
import com.credit.bladi.model.pricing.AgreedPricing.MourabahaTerms;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AgreedPricingRepository implements IAgreedPricingRepository {
    private final AgreedPricingDatasource datasource;

    public AgreedPricingRepository(AgreedPricingDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(AgreedPricing pricing) {
        AgreedPricingEntity entity = datasource.findById(pricing.demandId()).orElseGet(AgreedPricingEntity::new);
        entity.setDemandId(pricing.demandId());
        MortgageFees fees = pricing.mortgageFees();
        entity.setProcessingFeesRatio(fees != null ? toBigDecimal(fees.processingFeesRatio()) : null);
        entity.setMinProcessingFees(fees != null ? toBigDecimal(fees.minProcessingFees()) : null);
        entity.setMaxProcessingFees(fees != null ? toBigDecimal(fees.maxProcessingFees()) : null);
        entity.setRegistrationFeeRatio(fees != null ? toBigDecimal(fees.registrationFeeRatio()) : null);
        entity.setLandConservationRatio(fees != null ? toBigDecimal(fees.landConservationRatio()) : null);
        entity.setNotaryFeeRatio(fees != null ? toBigDecimal(fees.notaryFeeRatio()) : null);
        entity.setAdiMonthlyRate(fees != null ? toBigDecimal(fees.adiMonthlyRate()) : null);
        MourabahaTerms terms = pricing.mourabahaTerms();
        entity.setVatRate(terms != null ? toBigDecimal(terms.vatRate()) : null);
        entity.setTakafulMonthlyRate(terms != null ? toBigDecimal(terms.takafulMonthlyRate()) : null);
        InvestmentFees investment = pricing.investmentFees();
        entity.setInvestmentProcessingFeesRatio(
            investment != null ? toBigDecimal(investment.processingFeesRatio()) : null);
        entity.setInvestmentInsurancePremiumRatio(
            investment != null ? toBigDecimal(investment.insurancePremiumRatio()) : null);
        entity.setCapturedAt(pricing.capturedAt());
        entity.setCapturedBy(pricing.capturedBy());
        datasource.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgreedPricing> findByDemandId(Long demandId) {
        return datasource.findById(demandId).map(AgreedPricingRepository::toModel);
    }

    private static AgreedPricing toModel(AgreedPricingEntity entity) {
        MortgageFees fees = entity.getProcessingFeesRatio() == null ? null : new MortgageFees(
            toRatio(entity.getProcessingFeesRatio()), toMoney(entity.getMinProcessingFees()),
            toMoney(entity.getMaxProcessingFees()), toRatio(entity.getRegistrationFeeRatio()),
            toRatio(entity.getLandConservationRatio()), toRatio(entity.getNotaryFeeRatio()),
            toRatio(entity.getAdiMonthlyRate()));
        MourabahaTerms terms = entity.getVatRate() == null ? null : new MourabahaTerms(
            toRatio(entity.getVatRate()), toRate(entity.getTakafulMonthlyRate()));
        InvestmentFees investment = entity.getInvestmentProcessingFeesRatio() == null
            && entity.getInvestmentInsurancePremiumRatio() == null ? null : new InvestmentFees(
            toRatio(entity.getInvestmentProcessingFeesRatio()), toRatio(entity.getInvestmentInsurancePremiumRatio()));
        return new AgreedPricing(entity.getDemandId(), fees, terms, investment, entity.getCapturedAt(),
            entity.getCapturedBy());
    }
}
