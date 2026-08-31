package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_PASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_BOB;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import org.junit.jupiter.api.Test;

class UserModelTest {
    @Test
    void shouldBuildUserWithAllFields() {
        User user = User.getBuilder()
            .id(1L)
            .username(USER_ALICE)
            .password(PASSWORD_PASS)
            .role(UserRole.CLIENT)
            .build();

        assertEquals(1L, user.getId());
        assertEquals(USER_ALICE, user.getUsername());
        assertEquals(PASSWORD_PASS, user.getPassword());
        assertEquals(UserRole.CLIENT, user.getRole());
    }

    @Test
    void shouldBeEqualWhenSameFields() {
        User u1 = User.getBuilder().id(1L).username(USER_ALICE).password(PASSWORD_PASS).role(UserRole.CLIENT).build();
        User u2 = User.getBuilder().id(1L).username(USER_ALICE).password(PASSWORD_PASS).role(UserRole.CLIENT).build();

        assertEquals(u1, u2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentFields() {
        User u1 = User.getBuilder().id(1L).username(USER_ALICE).build();
        User u2 = User.getBuilder().id(2L).username(USER_BOB).build();

        assertNotEquals(u1, u2);
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        User u1 = User.getBuilder().username(USER_ALICE).build();
        User u2 = User.getBuilder().username(USER_ALICE).build();

        assertFalse(u1.equals(u2));
    }

    @Test
    void shouldBeEqualToItself() {
        User user = User.getBuilder().id(1L).build();
        assertTrue(user.equals(user));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        User user = User.getBuilder().id(1L).build();
        assertFalse(user.equals(USER_OTHER));
    }

    @Test
    void shouldHaveHashCode() {
        User user = User.getBuilder().id(1L).username(USER_ALICE).build();
        assertNotNull(user.hashCode());
    }

    @Test
    void shouldHaveToStringWithFieldNames() {
        User user = User.getBuilder().id(1L).username(USER_ALICE).role(UserRole.CLIENT).build();
        String str = user.toString();
        assertTrue(str.contains(USER_ALICE));
        assertTrue(str.contains(CODE_CLIENT));
    }
}
