package com.credit.bladi.controller.workflow;

import static com.credit.bladi.controller.workflow.mapper.DemandImportMapper.toResponseDTO;
import static com.credit.bladi.controller.workflow.mapper.DemandImportMapper.toStartResponseDTO;
import static com.credit.bladi.controller.workflow.mapper.UploadedFileMapper.toUploadedFile;

import com.credit.bladi.controller.dto.workflow.response.DemandImportReportResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandImportStartResponseDTO;
import com.credit.bladi.service.importdemand.IDemandImportService;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/demands/import")
@PreAuthorize("hasRole('MANAGER')")
public class DemandImportController implements IDemandImportController {
    private final IDemandImportService demandImportService;

    public DemandImportController(IDemandImportService demandImportService) {
        this.demandImportService = demandImportService;
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DemandImportStartResponseDTO> startImport(@RequestParam("file") MultipartFile file) {
        Long jobExecutionId = demandImportService.startImport(toUploadedFile(file));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toStartResponseDTO(jobExecutionId));
    }

    @Override
    @GetMapping("/{jobExecutionId}")
    public ResponseEntity<DemandImportReportResponseDTO> getReport(
            @Min(value = 1, message = "{validation.id.positive}") @PathVariable Long jobExecutionId) {
        return ResponseEntity.ok(toResponseDTO(demandImportService.getReport(jobExecutionId)));
    }
}
