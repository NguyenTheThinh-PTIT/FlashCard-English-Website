package com.education.flashEng.repository;

import com.education.flashEng.entity.ClassJoinRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassJoinRequestRepository extends JpaRepository<ClassJoinRequestEntity,Long> {
    Optional<ClassJoinRequestEntity> findByClassEntityIdAndUserEntityId(Long classId, Long requesterId);
}
