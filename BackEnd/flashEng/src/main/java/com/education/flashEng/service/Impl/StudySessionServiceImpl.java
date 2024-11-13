package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.ClassMemberEntity;
import com.education.flashEng.entity.StudySessionEntity;
import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.entity.WordEntity;
import com.education.flashEng.exception.EntityNotFoundWithIdException;
import com.education.flashEng.payload.request.StudySessionRequest;
import com.education.flashEng.payload.response.StatisticResponse;
import com.education.flashEng.repository.StudySessionRepository;
import com.education.flashEng.repository.WordRepository;
import com.education.flashEng.service.StudySessionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    @Override
    public boolean createStudySession(StudySessionRequest studySessionRequest) {
        UserEntity currentUser = userServiceImpl.getUserFromSecurityContext();
        StudySessionEntity studySessionEntity = new StudySessionEntity();
        System.out.println(studySessionRequest.getWordId());
        WordEntity wordEntity = wordRepository.findById(studySessionRequest.getWordId())
                .orElseThrow(() -> new EntityNotFoundWithIdException("Word", studySessionRequest.getWordId().toString()));
        if(Objects.equals(wordEntity.getSetEntity().getPrivacyStatus(), "CLASS")){
            List<ClassMemberEntity> classMemberEntities = wordEntity.getSetEntity().getClassEntity().getClassMemberEntityList();
            for(ClassMemberEntity classMemberEntity : classMemberEntities){
                if(Objects.equals(classMemberEntity.getUserEntity().getId(), currentUser.getId())){
                    studySessionEntity.setUserEntity(currentUser);
                    studySessionEntity.setWordEntity(wordEntity);
                    studySessionEntity.setDifficulty(studySessionRequest.getDifficulty());
                    StudySessionEntity studySession = studySessionRepository.save(studySessionEntity);
                    notificationServiceImpl.createStudySessionNotification(studySession, getReminderTimeBasedOnLevel(studySession.getDifficulty(),LocalDateTime.now()));
                    return true;
                }
            }
            throw new AccessDeniedException("You do not permission to learning this word");
        }
        else{
            studySessionEntity.setUserEntity(currentUser);
            studySessionEntity.setWordEntity(wordEntity);
            studySessionEntity.setDifficulty(studySessionRequest.getDifficulty());
            StudySessionEntity studySession = studySessionRepository.save(studySessionEntity);
            notificationServiceImpl.createStudySessionNotification(studySession, getReminderTimeBasedOnLevel(studySession.getDifficulty(),LocalDateTime.now()));
        }
        return true;
    }

    @Override
    public List<StatisticResponse> getDailyWordCountByUserId() {
        Long userId = userServiceImpl.getUserFromSecurityContext().getId();
        return studySessionRepository.findDailyWordCountByUserId(userId);
    }

    @Override
    public LocalDateTime getReminderTimeBasedOnLevel(String difficulty, LocalDateTime time) {
        LocalDateTime now = time;
        difficulty = difficulty.toLowerCase();
        return switch (difficulty) {
            case "very difficult" -> now.plusHours(3);
            case "difficult" -> now.plusDays(1);
            case "easy" -> now.plusDays(3);
            case "very easy" -> now.plusHours(5);
            default -> throw new IllegalArgumentException("Invalid Difficulty");
        };
    }
}
