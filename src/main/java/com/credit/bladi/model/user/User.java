package com.credit.bladi.model.user;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.SELF_ROLE_MODIFICATION_FORBIDDEN;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.enums.UserRole;

public class User {

    private Long id;

    private String username;

    private String password;

    private UserRole role;

    private User() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void assertRoleModifiableBy(String actorUsername) {
        if (username.equals(actorUsername)) {
            throw AuthException.of(SELF_ROLE_MODIFICATION_FORBIDDEN);
        }
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setRole(UserRole role) {
        this.role = role;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;

        private String username;

        private String password;

        private UserRole role;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(this.id);
            user.setUsername(this.username);
            user.setPassword(this.password);
            user.setRole(this.role);
            return user;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("User [id=%s, username=%s, role=%s]", id, username, role);
    }
}