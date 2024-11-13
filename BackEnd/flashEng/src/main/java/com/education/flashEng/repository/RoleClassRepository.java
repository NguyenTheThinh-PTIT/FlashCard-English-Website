package com.education.flashEng.repository;

import com.education.flashEng.entity.RoleClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleClassRepository extends JpaRepository<RoleClassEntity, Long> {
    Optional<RoleClassEntity> findByName(String name);
}
