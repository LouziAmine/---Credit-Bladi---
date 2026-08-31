package com.credit.bladi.controller.user;

import static com.credit.bladi.controller.user.mapper.UserControllerMapper.toCreditPage;
import static com.credit.bladi.controller.user.mapper.UserControllerMapper.toResponseDTO;
import static com.credit.bladi.controller.user.mapper.UserControllerMapper.toRole;
import static com.credit.bladi.controller.user.mapper.UserControllerMapper.toUser;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserProfileRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserRoleRequestDTO;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.service.user.IUserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(UserController.BASE_PATH)
@PreAuthorize("hasRole('MANAGER')")
public class UserController implements IUserController {
    static final String BASE_PATH = "/api/v1/users";

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditPage<UserResponseDTO>> getAllUsers(
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    ) {
        return ResponseEntity.ok(toCreditPage(userService.getAllUsers(page, size)));
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequestDTO dto) {
        UUID id = userService.createUser(toUser(dto), dto.gradeCode(), dto.teamCode());
        return ResponseEntity.created(URI.create(BASE_PATH + "/" + id)).build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        Long userId = userService.resolveUserId(id);
        return ResponseEntity.ok(toResponseDTO(userService.getUserById(userId)));
    }

    @Override
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long userId = userService.resolveUserId(id);
        userService.updateUserRole(userId, toRole(dto.role()), currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PatchMapping("/{id}/profile")
    public ResponseEntity<Void> updateUserProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserProfileRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long userId = userService.resolveUserId(id);
        userService.updateUserProfile(userId, dto.gradeCode(), dto.teamCode(), dto.email(),
            currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
