package com.credit.bladi.controller.simulation;

import static com.credit.bladi.controller.simulation.mapper.SimulationMapper.toDTO;
import static com.credit.bladi.controller.simulation.mapper.SimulationMapper.toDomain;

import com.credit.bladi.controller.dto.simulation.request.SimulationRequestDTO;
import com.credit.bladi.controller.dto.simulation.response.SimulationResponseDTO;
import com.credit.bladi.service.simulation.ISimulationService;
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
@RequestMapping("/api/v1/simulation")
public class SimulationController implements ISimulationController {

    private final ISimulationService simulationService;

    public SimulationController(ISimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    @PostMapping
    public ResponseEntity<SimulationResponseDTO> simulate(@Valid @RequestBody SimulationRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(toDTO(simulationService.simulate(toDomain(requestDTO))));
    }
}