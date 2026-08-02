package com.credit.bladi.repository.bam;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.bam.BamParametersPatchResult;
import java.util.Optional;

public interface IBamParametersRepository {

    CreditPage<BamParametersModel> findAll(int page, int size);

    Optional<BamParametersModel> findById(Long id);

    Optional<BamParametersModel> getActiveParameters();

    BamParametersPatchResult patchParameters(BamParametersModel bamParametersPatch);
}