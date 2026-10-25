package com.credit.bladi.service.workflow.document;

import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.model.workflow.InspectedFile;
import com.credit.bladi.model.workflow.UploadedFile;
import java.util.List;
import java.util.UUID;

public interface IDocumentService {
    DemandDocumentModel upload(Long demandId, UploadedFile file, String documentType);

    InspectedFile inspect(Long demandId, UploadedFile file);

    DemandDocumentModel record(Long demandId, InspectedFile file, String documentType);

    List<DemandDocumentModel> list(Long demandId);

    DownloadableDocument download(Long demandId, Long documentId);

    Long resolveDocumentId(UUID publicId);
}
