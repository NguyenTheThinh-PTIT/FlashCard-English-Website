package com.education.flashEng.repository;

import com.education.flashEng.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<WordEntity, Long> {
    List<WordEntity> findAllBySetEntityId(Long id);
}
