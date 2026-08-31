package com.credit.bladi.controller.documentsuggestion;

import static com.credit.bladi.controller.documentsuggestion.mapper.DocumentSuggestionControllerMapper.toResponseDTO;

import com.credit.bladi.controller.documentsuggestion.mapper.DocumentSuggestionControllerMapper;
import com.credit.bladi.controller.dto.documentsuggestion.request.CreateDocumentSuggestionRequestDTO;
import com.credit.bladi.controller.dto.documentsuggestion.response.DocumentSuggestionResponseDTO;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.service.documentsuggestion.IDocumentSuggestionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(DocumentSuggestionController.BASE_PATH)
public class DocumentSuggestionController implements IDocumentSuggestionController {
    static final String BASE_PATH = "/api/v1/document-suggestions";

    private final IDocumentSuggestionService documentSuggestionService;

    public DocumentSuggestionController(IDocumentSuggestionService documentSuggestionService) {
        this.documentSuggestionService = documentSuggestionService;
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DocumentSuggestionResponseDTO> createDocumentSuggestion(
            @Valid @RequestBody CreateDocumentSuggestionRequestDTO requestDTO) {
        DocumentSuggestionModel created = documentSuggestionService.createDocumentSuggestion(
            requestDTO.enterpriseType(), requestDTO.cspCode(), requestDTO.label());
        return ResponseEntity.created(URI.create(BASE_PATH + "/" + created.publicId()))
            .body(toResponseDTO(created));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DocumentSuggestionResponseDTO>> getDocumentSuggestions(
            @RequestParam(required = false) EnterpriseType enterpriseType,
            @RequestParam(required = false) CspCode cspCode) {
        return ResponseEntity.ok(documentSuggestionService.findSuggestions(enterpriseType, cspCode).stream()
            .map(DocumentSuggestionControllerMapper::toResponseDTO)
            .toList());
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteDocumentSuggestion(@PathVariable UUID id) {
        Long resolvedId = documentSuggestionService.resolveDocumentSuggestionId(id);
        documentSuggestionService.deleteDocumentSuggestion(resolvedId);
        return ResponseEntity.noContent().build();
    }
}
