package com.credit.bladi.datasource.user;

import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.model.enums.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDatasource extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByPublicId(UUID publicId);

    boolean existsByUsername(String username);

    long countByRole(UserRole role);

    List<UserEntity> findByRoleAndGradeIdAndTeamId(UserRole role, Long gradeId, Long teamId);

    List<UserEntity> findByRoleAndTeamId(UserRole role, Long teamId);

    List<UserEntity> findByRole(UserRole role);

    List<UserEntity> findByRoleAndEmail(UserRole role, String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.role = :role WHERE u.id = :id")
    int updateRole(@Param("id") Long id, @Param("role") UserRole role);

    @Modifying
    @Query("UPDATE UserEntity u SET u.gradeId = :gradeId, u.teamId = :teamId, u.email = :email WHERE u.id = :id")
    int updateProfile(@Param("id") Long id, @Param("gradeId") Long gradeId, @Param("teamId") Long teamId,
            @Param("email") String email);
}
