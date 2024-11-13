package com.education.flashEng.service;

import com.education.flashEng.entity.*;
import com.education.flashEng.payload.response.NotificationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface NotificationService {
    boolean createClassJoinRequestNotification(ClassJoinRequestEntity classJoinRequestEntity);

    boolean createClassInvitationNotification(ClassInvitationEntity classInvitationEntity);

    NotificationMetaDataEntity getNotificationMetaDataEntityByKeyAndValue(String key, String value);

    boolean deleteAllRelatedNotificationsByNotificationMetaData(String key, String value);

    boolean createAcceptedClassJoinRequestNotification(ClassJoinRequestEntity classJoinRequestEntity);

    boolean createRejectedClassJoinRequestNotification(ClassJoinRequestEntity classJoinRequestEntity);

    boolean createAcceptedClassInvitationNotification(ClassInvitationEntity classInvitationEntity);

    boolean createRejectedClassInvitationNotification(ClassInvitationEntity classInvitationEntity);

    boolean createClassSetRequestNotification(ClassSetRequestEntity classSetRequestEntity);

    boolean createAcceptRequestNotification(SetEntity setEntity);

    boolean createRejectRequestNotification(SetEntity setEntity, ClassEntity classEntity);

    boolean createStudySessionNotification(StudySessionEntity studySessionEntity, LocalDateTime localDateTime);

    List<NotificationResponse> getAllCurrentUserNotifications();
}
