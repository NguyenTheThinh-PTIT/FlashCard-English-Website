package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.ClassMemberEntity;
import com.education.flashEng.entity.StudySessionEntity;
import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.entity.WordEntity;
import com.education.flashEng.enums.AccessModifierType;
import com.education.flashEng.exception.EntityNotFoundWithIdException;
import com.education.flashEng.payload.request.StudySessionRequest;
import com.education.flashEng.payload.response.StatisticResponse;
import com.education.flashEng.repository.StudySessionRepository;
import com.education.flashEng.repository.WordRepository;
import com.education.flashEng.service.StudySessionService;
import com.education.flashEng.util.TimeUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudySessionServiceImpl implements StudySessionService {
    @Autowired
    private  UserServiceImpl userServiceImpl;
    @Autowired
    private  WordRepository wordRepository;
    @Autowired
    private  StudySessionRepository studySessionRepository;
    @Autowired
    private  NotificationServiceImpl notificationServiceImpl;
    @Autowired
    private TimeUtil timeUtil;

    @Transactional
    @Override
    public boolean createStudySession(StudySessionRequest studySessionRequest) {
        UserEntity currentUser = userServiceImpl.getUserFromSecurityContext();
        StudySessionEntity studySessionEntity = new StudySessionEntity();
        WordEntity wordEntity = wordRepository.findById(studySessionRequest.getWordId())
                .orElseThrow(() -> new EntityNotFoundWithIdException("Word", studySessionRequest.getWordId().toString()));
        String privacy = wordEntity.getSetEntity().getPrivacyStatus();
        if(!privacy.equals("PUBLIC")
                && !(privacy.equals("PRIVATE") && wordEntity.getSetEntity().getUserEntity().getId().equals(currentUser.getId()))
                && !(privacy.equals("CLASS") && wordEntity.getSetEntity().getClassEntity().getClassMemberEntityList().stream().anyMatch(classMemberEntity -> classMemberEntity.getUserEntity().getId().equals(currentUser.getId()))))
            throw new AccessDeniedException("You do not have permission to study this word");

        studySessionEntity.setUserEntity(currentUser);
        studySessionEntity.setWordEntity(wordEntity);
        studySessionEntity.setDifficulty(studySessionRequest.getDifficulty());
        StudySessionEntity studySession = studySessionRepository.save(studySessionEntity);
        notificationServiceImpl.createStudySessionNotification(studySessionEntity,getReminderTimeBasedOnLevel(studySession, LocalDateTime.now()));
        studySessionRepository.save(studySessionEntity);
        return true;
    }

    @Override
    public List<StatisticResponse> getDailyWordCountByUserId() {
        Long userId = userServiceImpl.getUserFromSecurityContext().getId();
        return studySessionRepository.findDailyWordCountByUserId(userId);
    }

    @Override
    public LocalDateTime getReminderTimeBasedOnLevel(StudySessionEntity studySessionEntity, LocalDateTime startTime) {
        Optional<StudySessionEntity> studySessionEntityOptional = studySessionRepository.findSecondNewestStudySessionEntityByWordEntityIdAndUserEntityId(studySessionEntity.getWordEntity().getId(), studySessionEntity.getUserEntity().getId());
        double reminderTime = studySessionEntity.getReminderTime();
        double coefficient = studySessionEntity.getCoefficient();
        if(studySessionEntityOptional.isPresent()){
            StudySessionEntity newestStudySession = studySessionEntityOptional.get();
            reminderTime = newestStudySession.getReminderTime();
            coefficient = newestStudySession.getCoefficient();
        }
        switch (studySessionEntity.getDifficulty().toLowerCase()){
            case "very easy":
                coefficient += 0.1;
                break;
            case "easy":
                break;
            case "difficult":
                if(coefficient > 0.3)
                    coefficient += -0.1;
                break;
            case "very difficult":
                coefficient = 1.0;
                reminderTime = 0.3;
                break;
        }
        reminderTime *= coefficient;
        LocalDateTime newRemindTime = timeUtil.addFractionOfDay(startTime, reminderTime);
        studySessionEntity.setCoefficient(coefficient);
        studySessionEntity.setReminderTime(reminderTime);
        return newRemindTime;
    }
}
