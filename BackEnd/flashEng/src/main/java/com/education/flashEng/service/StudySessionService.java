package com.education.flashEng.service;

import com.education.flashEng.entity.StudySessionEntity;
import com.education.flashEng.payload.request.StudySessionRequest;
import com.education.flashEng.payload.response.StatisticResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StudySessionService {
    boolean createStudySession(StudySessionRequest studySessionRequest);
    List<StatisticResponse> getDailyWordCountByUserId();

    LocalDateTime getReminderTimeBasedOnLevel(StudySessionEntity studySessionEntity, LocalDateTime startTime);
}
