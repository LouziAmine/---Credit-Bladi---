package com.credit.bladi.model.mourabaha;

import com.credit.bladi.model.parameters.VersionedPatchResult;

public record MourabahaParametersPatchResult(MourabahaParametersModel before, MourabahaParametersModel after)
    implements VersionedPatchResult<MourabahaParametersModel> {
}
