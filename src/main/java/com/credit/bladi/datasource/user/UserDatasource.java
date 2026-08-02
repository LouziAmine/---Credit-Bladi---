package com.credit.bladi.datasource.user;

import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.model.enums.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserDatasource extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(UserRole role);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.role = :role WHERE u.id = :id")
    int updateRole(@Param("id") Long id, @Param("role") UserRole role);
}