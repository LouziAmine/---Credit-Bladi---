package com.credit.bladi.controller.client;

import static com.credit.bladi.controller.client.mapper.EnterpriseControllerMapper.toCreditPage;
import static com.credit.bladi.controller.client.mapper.EnterpriseControllerMapper.toMatchResponseDTO;
import static com.credit.bladi.controller.client.mapper.EnterpriseControllerMapper.toResponseDTO;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.client.request.CreateEnterpriseRequestDTO;
import com.credit.bladi.controller.dto.client.response.EnterpriseMatchResponseDTO;
import com.credit.bladi.controller.dto.client.response.EnterpriseResponseDTO;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.service.client.IEnterpriseService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(EnterpriseController.BASE_PATH)
@PreAuthorize("hasRole('MANAGER')")
public class EnterpriseController implements IEnterpriseController {
    static final String BASE_PATH = "/api/v1/enterprises";

    private final IEnterpriseService enterpriseService;

    public EnterpriseController(IEnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    @Override
    @PostMapping
    public ResponseEntity<EnterpriseResponseDTO> createEnterprise(
            @Valid @RequestBody CreateEnterpriseRequestDTO requestDTO) {
        EnterpriseModel created = enterpriseService.createEnterprise(requestDTO.name(), requestDTO.sector(),
            requestDTO.registrationNumber());
        return ResponseEntity.created(URI.create(BASE_PATH + "/" + created.publicId()))
            .body(toResponseDTO(created));
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditPage<EnterpriseResponseDTO>> getAllEnterprises(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) Integer size) {
        return ResponseEntity.ok(toCreditPage(enterpriseService.getAllEnterprises(page, size)));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EnterpriseResponseDTO> getEnterpriseById(@PathVariable UUID id) {
        Long resolvedId = enterpriseService.resolveEnterpriseId(id);
        return ResponseEntity.ok(toResponseDTO(enterpriseService.getEnterpriseById(resolvedId)));
    }

    @Override
    @GetMapping("/match")
    public ResponseEntity<EnterpriseMatchResponseDTO> matchEnterpriseByName(
            @RequestParam(name = "name") String name) {
        return ResponseEntity.ok(toMatchResponseDTO(enterpriseService.matchEnterpriseByName(name)));
    }
}
