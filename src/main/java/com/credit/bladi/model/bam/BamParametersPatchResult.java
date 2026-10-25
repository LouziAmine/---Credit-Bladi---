package com.credit.bladi.model.bam;

import com.credit.bladi.model.parameters.VersionedPatchResult;

public record BamParametersPatchResult(BamParametersModel before, BamParametersModel after)
    implements VersionedPatchResult<BamParametersModel> {
}
