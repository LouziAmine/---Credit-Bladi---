package com.credit.bladi.repository.client;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.vo.Rib;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEnterpriseRepository {
    EnterpriseModel create(String name, String sector, String registrationNumber);

    CreditPage<EnterpriseModel> findAll(int page, int size);

    Optional<EnterpriseModel> findById(Long id);

    List<EnterpriseModel> findAllByIds(List<Long> ids);

    Optional<Long> findIdByPublicId(UUID publicId);

    boolean existsById(Long id);

    void updateRib(Long id, Rib rib);

    Optional<EnterpriseModel> findMatchByName(String rawName);

    Optional<EnterpriseModel> findByRegistrationNumber(String registrationNumber);
}
