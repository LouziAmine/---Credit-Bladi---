package com.credit.bladi.controller.client.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.client.response.EnterpriseMatchResponseDTO;
import com.credit.bladi.controller.dto.client.response.EnterpriseResponseDTO;
import com.credit.bladi.model.client.EnterpriseModel;
import java.util.Optional;

public final class EnterpriseControllerMapper {
    private EnterpriseControllerMapper() {
    }

    public static EnterpriseResponseDTO toResponseDTO(EnterpriseModel model) {
        return new EnterpriseResponseDTO(model.publicId(), model.name(), model.sector(), model.registrationNumber(),
            model.createdAt());
    }

    public static CreditPage<EnterpriseResponseDTO> toCreditPage(CreditPage<EnterpriseModel> source) {
        return CreditPageMapper.map(source, EnterpriseControllerMapper::toResponseDTO);
    }

    public static EnterpriseMatchResponseDTO toMatchResponseDTO(Optional<EnterpriseModel> match) {
        return match.map(model -> new EnterpriseMatchResponseDTO(true, toResponseDTO(model)))
            .orElse(new EnterpriseMatchResponseDTO(false, null));
    }
}
