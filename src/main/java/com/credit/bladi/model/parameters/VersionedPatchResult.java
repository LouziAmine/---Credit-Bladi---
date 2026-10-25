package com.credit.bladi.model.parameters;

public interface VersionedPatchResult<M extends VersionedParametersModel<M>> {
    M before();

    M after();
}
