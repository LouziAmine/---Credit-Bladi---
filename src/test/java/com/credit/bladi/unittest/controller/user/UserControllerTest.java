package com.credit.bladi.unittest.controller.user;

import static com.credit.bladi.unittest.mock.user.MockUser.createRequest;
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
        User user = User.getBuilder().id(1L).username("alice").role(UserRole.CLIENT).build();
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
        when(userService.createUser(any())).thenReturn(42L);

        ResponseEntity<Void> result = controller.createUser(createRequest());

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("/api/v1/users/42"), result.getHeaders().getLocation());
        verify(userService).createUser(any());
    }

    @Test
    void shouldReturn200WithUserById() {
        User user = User.getBuilder().id(1L).username("alice").role(UserRole.CLIENT).build();
        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<UserResponseDTO> result = controller.getUserById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("alice", result.getBody().username());
    }

    @Test
    void shouldReturn204WhenUserRoleUpdated() {
        UserDetails currentUser = mock(UserDetails.class);
        when(currentUser.getUsername()).thenReturn("admin");
        doNothing().when(userService).updateUserRole(eq(42L), eq(UserRole.MANAGER), anyString());

        ResponseEntity<Void> result = controller.updateUserRole(42L, updateRoleRequest(), currentUser);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).updateUserRole(42L, UserRole.MANAGER, "admin");
    }
}