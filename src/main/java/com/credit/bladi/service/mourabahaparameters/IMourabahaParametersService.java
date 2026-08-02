package com.credit.bladi.service.mourabahaparameters;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;

public interface IMourabahaParametersService {

    CreditPage<MourabahaParametersModel> getAllParameters(int page, int size);

    MourabahaParametersModel getById(Long id);

    MourabahaParametersModel patchParameters(MourabahaParametersModel mourabahaParametersPatch);
}
