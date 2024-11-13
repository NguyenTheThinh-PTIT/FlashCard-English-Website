package com.education.flashEng.repository;

import com.education.flashEng.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsernameAndStatus(String userName, int status);
    Optional<UserEntity> findByEmailAndStatus(String email, int status);

    Optional<UserEntity> findByIdAndStatus(long id, int status);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsernameAndStatus(String username, int status);

    boolean existsByEmailAndStatus(String email, int status);
}
