package com.credit.bladi.service.bamparameters;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.bam.BamParametersModel;

public interface IBamParametersService {

    CreditPage<BamParametersModel> getAllParameters(int page, int size);

    BamParametersModel getById(Long id);

    BamParametersModel patchParameters(BamParametersModel bamParametersPatch);
}
