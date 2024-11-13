package com.education.flashEng.service;


import com.education.flashEng.entity.ClassJoinRequestEntity;
import com.education.flashEng.payload.response.ClassJoinRequestResponse;

import java.util.Optional;

public interface ClassJoinRequestService {
     ClassJoinRequestEntity createClassJoinRequest(Long classId, Long userId);

     boolean acceptClassJoinRequest(Long requestId);

     boolean rejectClassJoinRequest(Long requestId);

     Optional<ClassJoinRequestEntity> getClassJoinRequestByClassIdAndUserId(Long classId, Long userId);

     ClassJoinRequestResponse getClassJoinRequest(Long requestId);

     String checkExistance(Long classId);

     boolean revokeClassJoinRequest(Long requestId);
}
