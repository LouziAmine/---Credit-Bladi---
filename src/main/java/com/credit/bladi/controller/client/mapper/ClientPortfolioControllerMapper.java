package com.credit.bladi.controller.client.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.enterprise.response.ClientDemandSummaryResponseDTO;
import com.credit.bladi.controller.dto.enterprise.response.ClientPortfolioEnterpriseResponseDTO;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.workflow.DemandModel;

public final class ClientPortfolioControllerMapper {
    private ClientPortfolioControllerMapper() {
    }

    public static ClientPortfolioEnterpriseResponseDTO toPortfolioEntryDTO(EnterpriseModel enterprise,
            EnterpriseUserLinkModel link) {
        return new ClientPortfolioEnterpriseResponseDTO(enterprise.publicId(), enterprise.name(),
            enterprise.sector(), link.role().name());
    }

    public static ClientDemandSummaryResponseDTO toDemandSummaryDTO(DemandModel model) {
        String enterpriseType = model.demandType() == DemandType.ENTERPRISE
            ? model.enterpriseDetail().enterpriseType().name() : null;
        return new ClientDemandSummaryResponseDTO(
            model.publicId(),
            model.reference(),
            enterpriseType,
            toBigDecimal(model.amount()),
            model.duration(),
            toBigDecimal(model.applicableRate()),
            model.createdAt(),
            model.completedAt(),
            model.isCompleted(),
            model.isRejected());
    }

    public static CreditPage<ClientDemandSummaryResponseDTO> toDemandSummaryCreditPage(CreditPage<DemandModel> source) {
        return CreditPageMapper.map(source, ClientPortfolioControllerMapper::toDemandSummaryDTO);
    }
}
