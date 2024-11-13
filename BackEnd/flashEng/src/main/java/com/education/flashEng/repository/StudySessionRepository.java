package com.education.flashEng.repository;

import com.education.flashEng.entity.StudySessionEntity;
import com.education.flashEng.payload.request.StudySessionRequest;
import com.education.flashEng.payload.response.StatisticResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySessionEntity, Long> {
    @Query("SELECT new com.education.flashEng.payload.response.StatisticResponse(FUNCTION('DATE', s.createdAt), COUNT(s)) " +
            "FROM StudySessionEntity s WHERE s.userEntity.id = :userId " +
            "GROUP BY FUNCTION('DATE', s.createdAt) ORDER BY FUNCTION('DATE', s.createdAt) DESC")
    List<StatisticResponse> findDailyWordCountByUserId(@Param("userId") Long userId);


}
