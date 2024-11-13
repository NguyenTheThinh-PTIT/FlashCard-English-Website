package com.education.flashEng.repository;

import com.education.flashEng.entity.ClassEntity;
import com.education.flashEng.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    List<ClassEntity> findAllByNameLike(String className);

}
