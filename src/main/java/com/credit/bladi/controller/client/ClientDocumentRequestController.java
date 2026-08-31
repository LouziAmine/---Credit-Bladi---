package com.credit.bladi.controller.client;

import static com.credit.bladi.controller.workflow.mapper.DocumentMapper.toDownloadResponse;
import static com.credit.bladi.controller.workflow.mapper.DocumentMapper.toResponseDTO;
import static com.credit.bladi.controller.workflow.mapper.UploadedFileMapper.toUploadedFile;

import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DocumentRequestResponseDTO;
import com.credit.bladi.controller.workflow.mapper.DocumentRequestMapper;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.service.security.ClientDemandAccessGuard;
import com.credit.bladi.service.workflow.document.IDocumentRequestService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/me/demands/{demandId}/document-requests")
@PreAuthorize("hasRole('CLIENT')")
public class ClientDocumentRequestController implements IClientDocumentRequestController {
    private final IDocumentRequestService documentRequestService;

    private final IWorkflowEngineService workflowEngineService;

    private final ClientDemandAccessGuard accessGuard;

    public ClientDocumentRequestController(IDocumentRequestService documentRequestService,
            IWorkflowEngineService workflowEngineService, ClientDemandAccessGuard accessGuard) {
        this.documentRequestService = documentRequestService;
        this.workflowEngineService = workflowEngineService;
        this.accessGuard = accessGuard;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DocumentRequestResponseDTO>> list(@PathVariable UUID demandId) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        accessGuard.assertApprovedForDemand(resolvedDemandId);
        List<DocumentRequestResponseDTO> requests = documentRequestService.listForDemand(resolvedDemandId).stream()
            .map(DocumentRequestMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(requests);
    }

    @Override
    @PostMapping(value = "/{requestId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DemandDocumentResponseDTO> uploadForRequest(
            @PathVariable UUID demandId,
            @PathVariable UUID requestId,
            @RequestParam("file") MultipartFile file) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        accessGuard.assertApprovedForDemand(resolvedDemandId);
        Long resolvedRequestId = documentRequestService.resolveRequestId(requestId);
        return ResponseEntity.ok(
            toResponseDTO(documentRequestService.uploadForRequest(resolvedDemandId, resolvedRequestId,
                toUploadedFile(file))));
    }

    @Override
    @GetMapping("/{requestId}/documents/download")
    public ResponseEntity<Resource> downloadFulfilledDocument(
            @PathVariable UUID demandId,
            @PathVariable UUID requestId) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        accessGuard.assertApprovedForDemand(resolvedDemandId);
        Long resolvedRequestId = documentRequestService.resolveRequestId(requestId);
        DownloadableDocument downloadable = documentRequestService.downloadFulfilledDocument(resolvedDemandId,
            resolvedRequestId);
        return toDownloadResponse(downloadable);
    }
}
