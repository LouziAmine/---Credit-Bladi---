package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.UserDetailsServiceAdapter;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.service.user.IUserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceAdapterTest {

    @Mock
    private IUserService userService;

    private UserDetailsServiceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserDetailsServiceAdapter(userService);
    }

    @Test
    void shouldLoadUserByUsernameWhenFound() {
        User user = User.getBuilder()
            .id(1L)
            .username("admin")
            .password("$2a$encodedpassword")
            .role(UserRole.MANAGER)
            .build();
        when(userService.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails userDetails = adapter.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("$2a$encodedpassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_MANAGER",
            userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
            () -> adapter.loadUserByUsername("unknown"));
    }
}