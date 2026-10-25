package com.credit.bladi.repository.bam;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.bam.BamParametersPatchResult;
import java.util.Optional;
import java.util.UUID;

public interface IBamParametersRepository {
    CreditPage<BamParametersModel> findAll(int page, int size);

    Optional<BamParametersModel> findById(Long id);

    Optional<Long> findIdByPublicId(UUID publicId);

    Optional<BamParametersModel> getActiveParameters();

    BamParametersPatchResult patchParameters(BamParametersModel bamParametersPatch);
}
