package com.education.flashEng.repository;

import com.education.flashEng.entity.StudySessionEntity;
import com.education.flashEng.payload.request.StudySessionRequest;
import com.education.flashEng.payload.response.StatisticResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudySessionRepository extends JpaRepository<StudySessionEntity, Long> {
    @Query("SELECT new com.education.flashEng.payload.response.StatisticResponse(FUNCTION('DATE', s.createdAt), COUNT(s)) " +
            "FROM StudySessionEntity s WHERE s.userEntity.id = :userId " +
            "GROUP BY FUNCTION('DATE', s.createdAt) ORDER BY FUNCTION('DATE', s.createdAt) DESC")
    List<StatisticResponse> findDailyWordCountByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM study_sessions s WHERE s.word_id = :wordId AND s.user_id = :userId ORDER BY s.created_at DESC LIMIT 1 OFFSET 1", nativeQuery = true)
    Optional<StudySessionEntity> findSecondNewestStudySessionEntityByWordEntityIdAndUserEntityId(@Param("wordId") Long wordId, @Param("userId") Long userId);
}
