package com.credit.bladi.repository.mourabaha;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersPatchResult;
import java.util.Optional;

public interface IMourabahaParametersRepository {

    CreditPage<MourabahaParametersModel> findAll(int page, int size);

    Optional<MourabahaParametersModel> findById(Long id);

    Optional<MourabahaParametersModel> getActiveParameters();

    MourabahaParametersPatchResult patchParameters(MourabahaParametersModel mourabahaParametersPatch);
}
