package com.credit.bladi.controller.invitation;

import static com.credit.bladi.controller.invitation.mapper.InvitationControllerMapper.toResponseDTO;

import com.credit.bladi.controller.dto.invitation.request.AcceptInvitationRequestDTO;
import com.credit.bladi.controller.dto.invitation.response.ClientInvitationPreviewResponseDTO;
import com.credit.bladi.service.invitation.IClientInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController implements IInvitationController {
    private final IClientInvitationService invitationService;

    public InvitationController(IClientInvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @Override
    @GetMapping("/{token}")
    public ResponseEntity<ClientInvitationPreviewResponseDTO> preview(
            @NotBlank(message = "{validation.token.notBlank}") @PathVariable String token) {
        return ResponseEntity.ok(toResponseDTO(invitationService.preview(token)));
    }

    @Override
    @PostMapping("/{token}/accept")
    public ResponseEntity<Void> accept(
            @NotBlank(message = "{validation.token.notBlank}") @PathVariable String token,
            @Valid @RequestBody AcceptInvitationRequestDTO requestDTO) {
        invitationService.accept(token, requestDTO.username(), requestDTO.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
