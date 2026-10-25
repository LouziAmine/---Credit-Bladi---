package com.credit.bladi.repository.mourabaha;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersPatchResult;
import java.util.Optional;
import java.util.UUID;

public interface IMourabahaParametersRepository {
    CreditPage<MourabahaParametersModel> findAll(int page, int size);

    Optional<MourabahaParametersModel> findById(Long id);

    Optional<Long> findIdByPublicId(UUID publicId);

    Optional<MourabahaParametersModel> getActiveParameters();

    MourabahaParametersPatchResult patchParameters(MourabahaParametersModel mourabahaParametersPatch);
}
