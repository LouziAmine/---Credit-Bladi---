package com.credit.bladi.service.bamparameters;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.bam.BamParametersModel;
import java.util.UUID;

public interface IBamParametersService {
    CreditPage<BamParametersModel> getAllParameters(int page, int size);

    BamParametersModel getById(Long id);

    Long resolveBamParametersId(UUID publicId);

    BamParametersModel patchParameters(BamParametersModel bamParametersPatch);
}
