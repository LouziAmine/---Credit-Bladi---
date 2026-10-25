package com.credit.bladi.controller.workflow;

import static com.credit.bladi.controller.workflow.mapper.DocumentRequestMapper.toResponseDTO;

import com.credit.bladi.controller.dto.workflow.request.CreateDocumentRequestRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.DocumentRequestResponseDTO;
import com.credit.bladi.controller.workflow.mapper.DocumentRequestMapper;
import com.credit.bladi.service.workflow.document.IDocumentRequestService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/demands/{id}/document-requests")
@PreAuthorize("hasRole('MANAGER')")
public class DocumentRequestController implements IDocumentRequestController {
    private final IDocumentRequestService documentRequestService;

    private final IWorkflowEngineService workflowEngineService;

    public DocumentRequestController(IDocumentRequestService documentRequestService,
            IWorkflowEngineService workflowEngineService) {
        this.documentRequestService = documentRequestService;
        this.workflowEngineService = workflowEngineService;
    }

    @Override
    @PostMapping
    public ResponseEntity<DocumentRequestResponseDTO> createRequest(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDocumentRequestRequestDTO requestDTO) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        return ResponseEntity.ok(
            toResponseDTO(documentRequestService.createRequest(demandId, requestDTO.documentType())));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DocumentRequestResponseDTO>> list(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        List<DocumentRequestResponseDTO> requests = documentRequestService.listForDemand(demandId).stream()
            .map(DocumentRequestMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(requests);
    }
}
