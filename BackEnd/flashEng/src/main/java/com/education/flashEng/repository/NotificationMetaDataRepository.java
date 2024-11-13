package com.education.flashEng.repository;

import com.education.flashEng.entity.NotificationMetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationMetaDataRepository extends JpaRepository<NotificationMetaDataEntity, Long> {
    public Optional<NotificationMetaDataEntity> findByKeyAndValue(String key, String value);
}
