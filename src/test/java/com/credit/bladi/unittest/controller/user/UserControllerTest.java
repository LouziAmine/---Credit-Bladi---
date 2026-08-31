package com.credit.bladi.unittest.controller.user;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.user.MockUser.createRequest;
import static com.credit.bladi.unittest.mock.user.MockUser.updateProfileRequest;
import static com.credit.bladi.unittest.mock.user.MockUser.updateRoleRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.controller.user.UserController;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.service.user.IUserService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private IUserService userService;

    private UserController controller;

    @BeforeEach
    void init() {
        controller = new UserController(userService);
    }

    @Test
    void shouldReturn200WithPagedUsers() {
        User user = User.getBuilder().id(1L).username(USER_ALICE).role(UserRole.CLIENT).build();
        CreditPage<User> page = new CreditPage<>();
        page.setItems(List.of(user));
        page.setTotal(1);
        page.setCurrentPage(1);
        page.setSize(20);
        when(userService.getAllUsers(1, 20)).thenReturn(page);

        ResponseEntity<CreditPage<UserResponseDTO>> result = controller.getAllUsers(1, 20);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getItems().size());
    }

    @Test
    void shouldReturn201WhenUserCreated() {
        UUID publicId = UUID.randomUUID();
        when(userService.createUser(any(), any(), any())).thenReturn(publicId);

        ResponseEntity<Void> result = controller.createUser(createRequest());

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("/api/v1/users/" + publicId), result.getHeaders().getLocation());
        verify(userService).createUser(any(), any(), any());
    }

    @Test
    void shouldReturn200WithUserById() {
        UUID publicId = UUID.randomUUID();
        User user = User.getBuilder().id(1L).publicId(publicId).username(USER_ALICE).role(UserRole.CLIENT).build();
        when(userService.resolveUserId(publicId)).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<UserResponseDTO> result = controller.getUserById(publicId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(USER_ALICE, result.getBody().username());
    }

    @Test
    void shouldReturn204WhenUserRoleUpdated() {
        UUID publicId = UUID.randomUUID();
        UserDetails currentUser = mock(UserDetails.class);
        when(currentUser.getUsername()).thenReturn(USER_ADMIN);
        when(userService.resolveUserId(publicId)).thenReturn(42L);
        doNothing().when(userService).updateUserRole(eq(42L), eq(UserRole.MANAGER), anyString());

        ResponseEntity<Void> result = controller.updateUserRole(publicId, updateRoleRequest(), currentUser);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).updateUserRole(42L, UserRole.MANAGER, USER_ADMIN);
    }

    @Test
    void shouldReturn204WhenUserProfileUpdated() {
        UUID publicId = UUID.randomUUID();
        UserDetails currentUser = mock(UserDetails.class);
        when(currentUser.getUsername()).thenReturn(USER_ADMIN);
        when(userService.resolveUserId(publicId)).thenReturn(42L);
        doNothing().when(userService).updateUserProfile(42L, CODE_ANALYSTE, CODE_PAE, null, USER_ADMIN);

        ResponseEntity<Void> result = controller.updateUserProfile(publicId, updateProfileRequest(CODE_ANALYSTE,
            CODE_PAE),
            currentUser);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).updateUserProfile(42L, CODE_ANALYSTE, CODE_PAE, null, USER_ADMIN);
    }
}
