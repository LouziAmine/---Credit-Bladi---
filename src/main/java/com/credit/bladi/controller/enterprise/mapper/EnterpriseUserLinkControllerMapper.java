package com.credit.bladi.controller.enterprise.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.enterprise.response.EnterpriseUserLinkResponseDTO;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;

public final class EnterpriseUserLinkControllerMapper {
    private EnterpriseUserLinkControllerMapper() {
    }

    public static EnterpriseUserLinkResponseDTO toResponseDTO(EnterpriseUserLinkModel model) {
        return new EnterpriseUserLinkResponseDTO(
            model.publicId(),
            model.enterprisePublicId(),
            model.userPublicId(),
            model.role().name(),
            model.status().name(),
            model.origin().name(),
            model.requestedByUsername(),
            model.decidedByUsername(),
            model.decidedAt(),
            model.createdAt(),
            model.username());
    }

    public static CreditPage<EnterpriseUserLinkResponseDTO> toCreditPage(CreditPage<EnterpriseUserLinkModel> source) {
        return CreditPageMapper.map(source, EnterpriseUserLinkControllerMapper::toResponseDTO);
    }
}
