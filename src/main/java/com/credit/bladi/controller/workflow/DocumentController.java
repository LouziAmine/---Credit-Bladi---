package com.credit.bladi.controller.workflow;

import static com.credit.bladi.controller.workflow.mapper.DocumentMapper.toDownloadResponse;
import static com.credit.bladi.controller.workflow.mapper.DocumentMapper.toResponseDTO;
import static com.credit.bladi.controller.workflow.mapper.UploadedFileMapper.toUploadedFile;

import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
import com.credit.bladi.controller.workflow.mapper.DocumentMapper;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.service.workflow.document.IDocumentService;
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
@RequestMapping("/api/v1/demands/{id}/documents")
@PreAuthorize("hasRole('MANAGER')")
public class DocumentController implements IDocumentController {
    private final IDocumentService documentService;

    private final IWorkflowEngineService workflowEngineService;

    public DocumentController(IDocumentService documentService, IWorkflowEngineService workflowEngineService) {
        this.documentService = documentService;
        this.workflowEngineService = workflowEngineService;
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DemandDocumentResponseDTO> upload(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "documentType", required = false) String documentType) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        return ResponseEntity.ok(toResponseDTO(documentService.upload(demandId, toUploadedFile(file), documentType)));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DemandDocumentResponseDTO>> list(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        List<DemandDocumentResponseDTO> documents = documentService.list(demandId).stream()
            .map(DocumentMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(documents);
    }

    @Override
    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> download(
            @PathVariable UUID id,
            @PathVariable UUID documentId) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        Long resolvedDocumentId = documentService.resolveDocumentId(documentId);
        DownloadableDocument downloadable = documentService.download(demandId, resolvedDocumentId);
        return toDownloadResponse(downloadable);
    }
}
