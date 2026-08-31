package com.credit.bladi.service.mourabahaparameters;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import java.util.UUID;

public interface IMourabahaParametersService {
    CreditPage<MourabahaParametersModel> getAllParameters(int page, int size);

    MourabahaParametersModel getById(Long id);

    Long resolveMourabahaParametersId(UUID publicId);

    MourabahaParametersModel patchParameters(MourabahaParametersModel mourabahaParametersPatch);
}
