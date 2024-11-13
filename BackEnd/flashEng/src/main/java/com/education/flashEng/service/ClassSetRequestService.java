package com.education.flashEng.service;

import com.education.flashEng.entity.ClassEntity;
import com.education.flashEng.entity.ClassSetRequestEntity;
import com.education.flashEng.entity.SetEntity;
import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.payload.response.SetResponse;

public interface ClassSetRequestService {
    ClassSetRequestEntity createSetRequest(SetEntity setEntity, UserEntity userEntity, ClassEntity classEntity);
    boolean acceptSetRequest(Long SetRequestId);
    boolean rejectSetRequest(Long SetRequestId);
    SetResponse getSetRequest(Long setRequestId);
}
