package com.education.flashEng.repository;

import com.education.flashEng.entity.ClassSetRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSetRequestRepository extends JpaRepository<ClassSetRequestEntity, Long> {
    ClassSetRequestEntity findBySetEntityId(Long id);
}
