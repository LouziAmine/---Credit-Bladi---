package com.credit.bladi.model.user;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.SELF_PROFILE_MODIFICATION_FORBIDDEN;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.SELF_ROLE_MODIFICATION_FORBIDDEN;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import java.util.UUID;

public final class User {
    private static final String SUPERVISOR_GRADE_CODE = "SUPERVISEUR";

    private final Long id;

    private final UUID publicId;

    private final String username;

    private final String fullName;

    private final String email;

    private final String password;

    private final UserRole role;

    private final Long gradeId;

    private final String gradeCode;

    private final String gradeName;

    private final Long teamId;

    private final String teamCode;

    private final String teamName;

    private final Language preferredLanguage;

    private User(Builder builder) {
        this.id = builder.id;
        this.publicId = builder.publicId;
        this.username = builder.username;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.gradeId = builder.gradeId;
        this.gradeCode = builder.gradeCode;
        this.gradeName = builder.gradeName;
        this.teamId = builder.teamId;
        this.teamCode = builder.teamCode;
        this.teamName = builder.teamName;
        this.preferredLanguage = builder.preferredLanguage;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public String getGradeCode() {
        return gradeCode;
    }

    public String getGradeName() {
        return gradeName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public String getTeamName() {
        return teamName;
    }

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public boolean isEligibleFor(WorkflowStepInfo step) {
        return gradeId != null && gradeId.equals(step.requiredGradeId())
            && teamId != null && teamId.equals(step.requiredTeamId());
    }

    public boolean isMemberOf(Long team) {
        return team != null && team.equals(teamId);
    }

    public boolean isSupervisorOf(Long team) {
        return isMemberOf(team) && SUPERVISOR_GRADE_CODE.equals(gradeCode);
    }

    public boolean hasGradeAndTeam() {
        return gradeId != null && teamId != null;
    }

    public void assertRoleModifiableBy(String actorUsername) {
        if (username.equals(actorUsername)) {
            throw AuthException.of(SELF_ROLE_MODIFICATION_FORBIDDEN);
        }
    }

    public void assertProfileModifiableBy(String actorUsername) {
        if (username.equals(actorUsername)) {
            throw AuthException.of(SELF_PROFILE_MODIFICATION_FORBIDDEN);
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;

        private UUID publicId;

        private String username;

        private String fullName;

        private String email;

        private String password;

        private UserRole role;

        private Long gradeId;

        private String gradeCode;

        private String gradeName;

        private Long teamId;

        private String teamCode;

        private String teamName;

        private Language preferredLanguage;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder publicId(UUID publicId) {
            this.publicId = publicId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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

        public Builder gradeId(Long gradeId) {
            this.gradeId = gradeId;
            return this;
        }

        public Builder gradeCode(String gradeCode) {
            this.gradeCode = gradeCode;
            return this;
        }

        public Builder gradeName(String gradeName) {
            this.gradeName = gradeName;
            return this;
        }

        public Builder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public Builder teamCode(String teamCode) {
            this.teamCode = teamCode;
            return this;
        }

        public Builder teamName(String teamName) {
            this.teamName = teamName;
            return this;
        }

        public Builder preferredLanguage(Language preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
            return this;
        }

        public User build() {
            return new User(this);
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
