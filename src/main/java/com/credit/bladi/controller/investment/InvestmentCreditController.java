package com.credit.bladi.controller.investment;

import static com.credit.bladi.controller.investment.mapper.InvestmentCreditMapper.toDTO;
import static com.credit.bladi.controller.investment.mapper.InvestmentCreditMapper.toDomain;

import com.credit.bladi.controller.dto.investment.request.InvestmentCreditRequestDTO;
import com.credit.bladi.controller.dto.investment.response.InvestmentCreditResponseDTO;
import com.credit.bladi.service.investment.IInvestmentCreditService;
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
@RequestMapping("/api/v1/investment-credit")
public class InvestmentCreditController implements IInvestmentCreditController {
    private final IInvestmentCreditService investmentCreditService;

    public InvestmentCreditController(IInvestmentCreditService investmentCreditService) {
        this.investmentCreditService = investmentCreditService;
    }

    @Override
    @PostMapping
    public ResponseEntity<InvestmentCreditResponseDTO> simulate(
            @Valid @RequestBody InvestmentCreditRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(toDTO(investmentCreditService.simulate(toDomain(requestDTO))));
    }
}
