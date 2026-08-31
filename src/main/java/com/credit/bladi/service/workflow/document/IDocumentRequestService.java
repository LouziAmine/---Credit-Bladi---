package com.credit.bladi.service.workflow.document;

import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DocumentRequestModel;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.model.workflow.UploadedFile;
import java.util.List;
import java.util.UUID;

public interface IDocumentRequestService {
    DocumentRequestModel createRequest(Long demandId, String documentType);

    Long resolveRequestId(UUID publicId);

    List<DocumentRequestModel> listForDemand(Long demandId);

    DemandDocumentModel uploadForRequest(Long demandId, Long requestId, UploadedFile file);

    DownloadableDocument downloadFulfilledDocument(Long demandId, Long requestId);
}
