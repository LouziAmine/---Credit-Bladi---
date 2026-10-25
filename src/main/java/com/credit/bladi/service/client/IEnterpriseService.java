package com.credit.bladi.service.client;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.client.EnterpriseModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEnterpriseService {
    EnterpriseModel createEnterprise(String name, String sector, String registrationNumber);

    CreditPage<EnterpriseModel> getAllEnterprises(int page, int size);

    EnterpriseModel getEnterpriseById(Long id);

    List<EnterpriseModel> getEnterprisesByIds(List<Long> ids);

    String getRibOrNull(Long enterpriseId);

    Long resolveEnterpriseId(UUID publicId);

    Optional<EnterpriseModel> matchEnterpriseByName(String rawName);
}
