package com.credit.bladi.controller.mortgage;

import static com.credit.bladi.controller.mortgage.mapper.MortgageCreditMapper.toDTO;
import static com.credit.bladi.controller.mortgage.mapper.MortgageCreditMapper.toDomain;

import com.credit.bladi.controller.dto.mortgage.request.MortgageCreditRequestDTO;
import com.credit.bladi.controller.dto.mortgage.response.MortgageCreditResponseDTO;
import com.credit.bladi.service.mortgage.IMortgageCreditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/mortgage-credit")
public class MortgageCreditController implements IMortgageCreditController {
    private final IMortgageCreditService simulationService;

    public MortgageCreditController(IMortgageCreditService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    @PostMapping
    public ResponseEntity<MortgageCreditResponseDTO> simulate(@Valid @RequestBody MortgageCreditRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(toDTO(simulationService.simulate(toDomain(requestDTO))));
    }
}
