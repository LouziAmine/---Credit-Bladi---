package com.credit.bladi.controller.workflow;

import static com.credit.bladi.controller.user.mapper.UserControllerMapper.toResponseDTO;
import static com.credit.bladi.controller.workflow.mapper.DemandMapper.toCreditPage;
import static com.credit.bladi.controller.workflow.mapper.DemandMapper.toResponseDTO;
import static com.credit.bladi.controller.workflow.mapper.DemandMapper.toStepClosureDetails;
import static com.credit.bladi.model.vo.ValueObjectMappers.toMoney;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.controller.dto.workflow.request.AddDemandCommentRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.AdvanceDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.AssignDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.ClassifyDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAmortizationResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAssignmentHistoryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandCommentResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandHistoryEntryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandPipelineStepResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandStepResponseDTO;
import com.credit.bladi.controller.workflow.mapper.DemandMapper;
import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.enums.DemandStatus;
import com.credit.bladi.model.enums.StepDirection;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.service.workflow.engine.IDemandTimelineService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.service.workflow.product.DemandAmortizationProviderFactory;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
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
@RequestMapping(DemandController.BASE_PATH)
@PreAuthorize("hasRole('MANAGER')")
public class DemandController implements IDemandController {
    static final String BASE_PATH = "/api/v1/demands";

    private static final String COMMENTS_PATH = "/{id}/comments";

    private final IWorkflowEngineService workflowEngineService;

    private final IDemandTimelineService timelineService;

    private final DemandAmortizationProviderFactory amortizationProviderFactory;

    public DemandController(IWorkflowEngineService workflowEngineService, IDemandTimelineService timelineService,
                            DemandAmortizationProviderFactory amortizationProviderFactory) {
        this.workflowEngineService = workflowEngineService;
        this.timelineService = timelineService;
        this.amortizationProviderFactory = amortizationProviderFactory;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditPage<DemandResponseDTO>> getAllDemands(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) Integer size,
            @RequestParam(name = "enterpriseId", required = false) UUID enterpriseId,
            @RequestParam(name = "companyName", required = false) String companyName,
            @RequestParam(name = "status", required = false) DemandStatus status,
            @RequestParam(name = "minAmount", required = false) BigDecimal minAmount,
            @RequestParam(name = "maxAmount", required = false) BigDecimal maxAmount) {
        CreditPage<DemandModel> demands = workflowEngineService.getAllDemands(page, size, enterpriseId, companyName,
            status, toMoney(minAmount), toMoney(maxAmount));
        List<Long> demandIds = demands.getItems().stream().map(DemandModel::id).toList();
        Map<Long, DemandStepSummary> currentSteps = workflowEngineService.getCurrentStepSummaries(demandIds);
        return ResponseEntity.ok(toCreditPage(demands, currentSteps));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DemandResponseDTO> getById(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        return ResponseEntity.ok(toResponseDTO(workflowEngineService.getDemand(demandId)));
    }

    @Override
    @GetMapping("/{id}/current-step")
    public ResponseEntity<DemandStepResponseDTO> getCurrentStep(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        return ResponseEntity.ok(toResponseDTO(workflowEngineService.getCurrentStep(demandId)));
    }

    @Override
    @GetMapping("/{id}/history")
    public ResponseEntity<List<DemandHistoryEntryResponseDTO>> getHistory(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        List<DemandHistoryEntryResponseDTO> history = timelineService.getHistory(demandId).stream()
            .map(DemandMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(history);
    }

    @Override
    @GetMapping("/{id}/pipeline")
    public ResponseEntity<List<DemandPipelineStepResponseDTO>> getPipeline(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        List<DemandPipelineStepResponseDTO> pipeline = timelineService.getPipeline(demandId).stream()
            .map(DemandMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(pipeline);
    }

    @Override
    @PostMapping("/{id}/advance")
    public ResponseEntity<DemandResponseDTO> advance(@PathVariable UUID id,
            @Valid @RequestBody AdvanceDemandRequestDTO requestDTO) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        DemandModel advanced = workflowEngineService.advance(demandId, toStepClosureDetails(requestDTO),
            requestDTO.assigneeUsername());
        return ResponseEntity.ok(toResponseDTO(advanced));
    }

    @Override
    @PostMapping("/{id}/classify")
    public ResponseEntity<DemandResponseDTO> classify(@PathVariable UUID id,
            @Valid @RequestBody ClassifyDemandRequestDTO requestDTO) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        DemandModel classified = workflowEngineService.classify(demandId,
            ClassificationOutcome.valueOf(requestDTO.classificationOutcome()), requestDTO.enterpriseId(),
            requestDTO.note(), requestDTO.assigneeUsername(), requestDTO.clientRib());
        return ResponseEntity.ok(toResponseDTO(classified));
    }

    @Override
    @GetMapping("/{id}/amortization")
    public ResponseEntity<DemandAmortizationResponseDTO> getAmortizationSchedule(
            @PathVariable UUID id,
            @RequestParam(required = false) BigDecimal rate,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) Integer duration) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        DemandModel demand = workflowEngineService.getDemand(demandId);
        Money amountOverride = toMoney(amount);
        Rate rateOverride = toRate(rate);

        DemandAmortizationResult result = amortizationProviderFactory.get(demand.demandType())
            .getAmortizationSchedule(demand, amountOverride, rateOverride, duration);
        return ResponseEntity.ok(DemandMapper.toAmortizationResponseDTO(result));
    }

    @Override
    @PostMapping("/{id}/assign")
    public ResponseEntity<DemandStepResponseDTO> assign(@PathVariable UUID id,
            @Valid @RequestBody AssignDemandRequestDTO requestDTO) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        return ResponseEntity.ok(toResponseDTO(workflowEngineService.assign(demandId, requestDTO.username())));
    }

    @Override
    @GetMapping("/{id}/assignment-history")
    public ResponseEntity<List<DemandAssignmentHistoryResponseDTO>> getAssignmentHistory(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        List<DemandAssignmentHistoryResponseDTO> history = timelineService.getAssignmentHistory(demandId)
            .stream()
            .map(DemandMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(history);
    }

    @Override
    @GetMapping("/{id}/eligible-assignees")
    public ResponseEntity<List<UserResponseDTO>> getEligibleAssignees(@PathVariable UUID id,
            @RequestParam(name = "direction", defaultValue = "CURRENT") StepDirection direction) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        List<UserResponseDTO> eligible = workflowEngineService.getEligibleAssignees(demandId, direction).stream()
            .map(user -> toResponseDTO(user))
            .toList();
        return ResponseEntity.ok(eligible);
    }

    @Override
    @GetMapping(COMMENTS_PATH)
    public ResponseEntity<List<DemandCommentResponseDTO>> getComments(@PathVariable UUID id) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        List<DemandCommentResponseDTO> comments = timelineService.getComments(demandId).stream()
            .map(DemandMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(comments);
    }

    @Override
    @PostMapping(COMMENTS_PATH)
    public ResponseEntity<DemandCommentResponseDTO> addComment(@PathVariable UUID id,
            @Valid @RequestBody AddDemandCommentRequestDTO requestDTO) {
        Long demandId = workflowEngineService.resolveDemandId(id);
        DemandCommentResponseDTO created = toResponseDTO(timelineService.addComment(demandId,
            requestDTO.comment()));
        return ResponseEntity.created(URI.create(BASE_PATH + "/" + id + "/comments/" + created.id()))
            .body(created);
    }
}
