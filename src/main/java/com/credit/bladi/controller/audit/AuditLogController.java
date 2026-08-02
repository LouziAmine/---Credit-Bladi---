package com.credit.bladi.controller.audit;

import static com.credit.bladi.controller.audit.mapper.AuditLogMapper.toCreditPage;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import com.credit.bladi.service.audit.IAuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/audit-log")
@PreAuthorize("hasRole('MANAGER')")
public class AuditLogController implements IAuditLogController {

    private final IAuditService auditService;

    public AuditLogController(IAuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditPage<AuditLogResponseDTO>> getAuditLog(
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    ) {
        return ResponseEntity.ok(toCreditPage(auditService.getAuditLog(page, size)));
    }
}
