package com.credit.bladi.service.pricing;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.pricing.AgreedPricing;
import com.credit.bladi.model.pricing.AgreedPricing.InvestmentFees;
import com.credit.bladi.model.pricing.AgreedPricing.MortgageFees;
import com.credit.bladi.model.pricing.AgreedPricing.MourabahaTerms;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.event.CommitteeDecisionEvent;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.repository.investment.IInvestmentCreditParametersRepository;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.repository.pricing.IAgreedPricingRepository;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.time.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AgreedPricingCapture {
    private static final Logger LOGGER = LogManager.getLogger(AgreedPricingCapture.class);

    private final IWorkflowEngineService workflowEngineService;

    private final IBamParametersRepository bamParametersRepository;

    private final IMourabahaParametersRepository mourabahaParametersRepository;

    private final IInvestmentCreditParametersRepository investmentParametersRepository;

    private final IAgreedPricingRepository agreedPricingRepository;

    private final Clock clock;

    public AgreedPricingCapture(IWorkflowEngineService workflowEngineService,
            IBamParametersRepository bamParametersRepository,
            IMourabahaParametersRepository mourabahaParametersRepository,
            IInvestmentCreditParametersRepository investmentParametersRepository,
            IAgreedPricingRepository agreedPricingRepository, Clock clock) {
        this.workflowEngineService = workflowEngineService;
        this.bamParametersRepository = bamParametersRepository;
        this.mourabahaParametersRepository = mourabahaParametersRepository;
        this.investmentParametersRepository = investmentParametersRepository;
        this.agreedPricingRepository = agreedPricingRepository;
        this.clock = clock;
    }

    @EventListener
    public void onCommitteeDecision(CommitteeDecisionEvent event) {
        DemandModel demand = workflowEngineService.getDemand(event.demandId());
        MortgageFees mortgageFees = null;
        MourabahaTerms mourabahaTerms = null;
        InvestmentFees investmentFees = null;
        if (demand.demandType() == DemandType.MORTGAGE) {
            mortgageFees = bamParametersRepository.getActiveParameters().map(MortgageFees::of).orElse(null);
            if (demand.isMourabaha()) {
                mourabahaTerms = mourabahaParametersRepository.getActiveParameters()
                    .map(MourabahaTerms::of).orElse(null);
            }
        } else {
            investmentFees = investmentParametersRepository
                .findActiveByEnterpriseType(demand.enterpriseDetail().enterpriseType())
                .map(InvestmentFees::of).orElse(null);
        }
        agreedPricingRepository.save(new AgreedPricing(demand.id(), mortgageFees, mourabahaTerms, investmentFees,
            clock.instant(), event.decidedByUsername()));
        LOGGER.info("AgreedPricingCaptured - demandId={}, mortgageFees={}, mourabahaTerms={}, investmentFees={}",
            demand.id(), mortgageFees != null, mourabahaTerms != null, investmentFees != null);
    }
}
