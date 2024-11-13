package com.education.flashEng.repository;

import com.education.flashEng.entity.ClassMemberEntity;
import com.education.flashEng.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassMemberRepository extends JpaRepository<ClassMemberEntity,Long> {
    Optional<ClassMemberEntity> findByClassEntityIdAndUserEntityId(Long classId, Long userId);
}
